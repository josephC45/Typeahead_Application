package com.typeahead.trie_microservice.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.typeahead.trie_microservice.pojo.Trie;
import com.typeahead.trie_microservice.service.KafkaProducer;

public class MyWebsocketHandler extends TextWebSocketHandler {

    private Trie trie;
    private KafkaProducer kafkaProducer;

    public MyWebsocketHandler(Trie trie, KafkaProducer kafkaProducer){
        this.trie = trie;
        this.kafkaProducer = new KafkaProducer();
    }
    public void queryTrie(WebSocketSession session, TextMessage textMessage){
        StringBuilder prefix = new StringBuilder();
        if(textMessage.getPayload().matches("[^A-Za-z]+")) prefix.setLength(0);
        else {
            System.out.println("Querying trie with prefix: " + textMessage.getPayload());
            prefix.append(textMessage.getPayload());
            trie.getPrefix(prefix.toString());
        }
        //Idea to send prefix via kafka which will then be ingested by spark (microbatching)
        kafkaProducer.sendPrefix(prefix.toString());
    }
}
