package com.typeahead.trie_microservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.typeahead.trie_microservice.websocket.TrieWebsocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    private final TrieWebsocketHandler trieWebsocketHandler;
    
    public WebSocketConfig(TrieWebsocketHandler trieWebsocketHandler){
        this.trieWebsocketHandler = trieWebsocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(trieWebsocketHandler, "/ws").setAllowedOrigins("http://localhost:8080");
    }

}
