package com.typeahead.trie_microservice.websocket;

import java.io.IOException;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.typeahead.trie_microservice.service.KafkaProducerService;
import com.typeahead.trie_microservice.service.TrieService;

public class TrieWebsocketHandler extends TextWebSocketHandler {

    private TrieService trieService;
    private KafkaProducerService kafkaService;
    private StringBuilder wordTyped = new StringBuilder();

    public TrieWebsocketHandler(TrieService trieService, KafkaProducerService kafkaService){
        this.trieService = trieService;
        this.kafkaService = kafkaService;
    }

    private boolean isEndOfWord(String currentPrefix){
        if(currentPrefix.matches("[^A-Za-z]")) return true;
        return false;
    }

    private void sendResponseToClient(WebSocketSession session, TextMessage response){
        try {
            session.sendMessage(response);
        } catch (IOException e) {
            System.err.println("Failed to send to websocket client: " + e.getMessage());
        }
    }

    public void queryTrie(WebSocketSession session, TextMessage textMessage){
        //Idea to send prefix via kafka which will then be ingested by spark (microbatching)
        String currentPrefix = textMessage.getPayload().toString();
        if(!isEndOfWord(currentPrefix)) {
            System.out.println("Querying trie with prefix: " + currentPrefix);
            List<String> popularAssociatedWordsWithPrefix = trieService.getPopularPrefixes(currentPrefix);

            TextMessage response = (popularAssociatedWordsWithPrefix.isEmpty())
                ? new TextMessage("No popular prefixes") 
                : new TextMessage(String.join(",", popularAssociatedWordsWithPrefix));

            sendResponseToClient(session, response);
        }else{
            kafkaService.sendMessageToKafka(wordTyped.toString());
            wordTyped.setLength(0);
        }
    }
}
