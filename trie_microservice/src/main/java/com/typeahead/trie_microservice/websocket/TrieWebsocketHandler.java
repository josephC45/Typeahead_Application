package com.typeahead.trie_microservice.websocket;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.typeahead.trie_microservice.service.KafkaProducerService;
import com.typeahead.trie_microservice.service.TrieService;

@Component
public class TrieWebsocketHandler extends TextWebSocketHandler {

    private final TrieService trieService;
    private final KafkaProducerService kafkaService;
    private final StringBuilder wordTyped = new StringBuilder();

    public TrieWebsocketHandler(TrieService trieService, KafkaProducerService kafkaService){
        this.trieService = trieService;
        this.kafkaService = kafkaService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connection established with client: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Error in WebSocket transport for client: " + session.getId() + " - " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket connection closed for client: " + session.getId());
    }

    private boolean isEndOfWord(String currentPrefix){
        return currentPrefix.matches("[^A-Za-z]");
    }

    private void sendResponseToClient(WebSocketSession session, TextMessage response){
        try {
            session.sendMessage(response);
        } catch (IOException e) {
            System.err.println("Failed to send to websocket client: " + e.getMessage());
        }
    }

    private void queryTrie(WebSocketSession session, String currentPrefix){
        //Idea to send prefix via kafka which will then be ingested by spark (microbatching)
        if(!isEndOfWord(currentPrefix)) {
            System.out.println("Querying trie with prefix: " + currentPrefix);
            List<String> popularAssociatedWordsWithPrefix = trieService.getPopularPrefixes(currentPrefix);

            TextMessage response = (popularAssociatedWordsWithPrefix.isEmpty())
                ? new TextMessage("No popular prefixes") 
                : new TextMessage(String.join(",", popularAssociatedWordsWithPrefix));

            sendResponseToClient(session, response);
            wordTyped.append(currentPrefix);
        }else{
            kafkaService.sendMessageToKafka(wordTyped.toString());
            wordTyped.setLength(0);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage){
        String currentPrefix = textMessage.getPayload();
        System.out.println("Received message: " + currentPrefix + " from client: " + session.getId());
        queryTrie(session, currentPrefix);
    }
}
