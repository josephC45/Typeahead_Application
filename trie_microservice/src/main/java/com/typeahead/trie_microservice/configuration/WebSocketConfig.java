package com.typeahead.trie_microservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.typeahead.trie_microservice.service.KafkaProducerService;
import com.typeahead.trie_microservice.service.TrieService;
import com.typeahead.trie_microservice.websocket.TrieWebsocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    private TrieService trieService;
    private KafkaProducerService kafkaProducerService;

    public WebSocketConfig(TrieService trieService, KafkaProducerService kafkaProducerService){
        this.trieService = trieService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TrieWebsocketHandler(trieService, kafkaProducerService), "/ws").setAllowedOrigins("*");
    }

}
