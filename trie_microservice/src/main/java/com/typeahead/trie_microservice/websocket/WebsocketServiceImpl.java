package com.typeahead.trie_microservice.websocket;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.typeahead.trie_microservice.domain.TrieService;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    private final TrieService trieService;
    private final KafkaProducerService kafkaService;
    private final StringBuilder wordTyped = new StringBuilder();

    public WebsocketServiceImpl(TrieService trieService, KafkaProducerService kafkaService){
        this.trieService = trieService;
        this.kafkaService = kafkaService;
    }

    @Override
    public boolean isEndOfWord(String currentPrefix){
        return currentPrefix.matches("[^A-Za-z']");
    }

    @Override
    public void sendResponseToClient(WebSocketSession session, TextMessage response){
        try {
            session.sendMessage(response);
        } catch (IOException e) {
            System.err.println("Failed to send to websocket client: " + e.getMessage());
        }
    }

    @Override
    public void queryTrie(WebSocketSession session, String currentPrefix){
        //Idea to send prefix via kafka which will then be ingested by spark (microbatching)
        if(!isEndOfWord(currentPrefix)) {
            wordTyped.append(currentPrefix);
            String curWordTyped = wordTyped.toString();
            System.out.println("Querying trie with prefix: " + curWordTyped);
            List<String> popularAssociatedWordsWithPrefix = trieService.getPopularPrefixes(curWordTyped);

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
