package com.typeahead.trie_microservice.typeahead_trie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.typeahead.trie_microservice.typeahead_trie.handler.MyWebsocketHandler;
import com.typeahead.trie_microservice.typeahead_trie.pojo.Trie;
import com.typeahead.trie_microservice.typeahead_trie.service.KafkaProducer;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    private Trie trie;
    private KafkaProducer kafkaProducer;

    public WebSocketConfig(Trie trie, KafkaProducer kafkaProducer){
        this.trie = trie;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebsocketHandler(trie, kafkaProducer), "/ws").setAllowedOrigins("*");
    }

}
