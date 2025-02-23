package com.typeahead.trie_microservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Configuration
@EnableWebSocket
class WebsocketConfig implements WebSocketConfigurer {

    private final AbstractWebSocketHandler websocketHandler;
    private final WebsocketProperties websocketProperties;

    WebsocketConfig(AbstractWebSocketHandler websocketHandler, WebsocketProperties websocketProperties) {
        this.websocketHandler = websocketHandler;
        this.websocketProperties = websocketProperties;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(websocketHandler, websocketProperties.getHandlerPath())
                .setAllowedOrigins(websocketProperties.getHandlerOrigins());
    }

}
