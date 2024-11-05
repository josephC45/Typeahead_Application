package com.typeahead.trie_microservice.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    private final WebsocketService websocketService;
    private static final Logger logger = LogManager.getLogger(WebsocketHandler.class);

    public WebsocketHandler(WebsocketService websocketService) {
        this.websocketService = websocketService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket connection established with client: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Error in WebSocket transport for client: " + session.getId() + " - " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket connection closed for client: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        String currentPrefix = textMessage.getPayload();
        websocketService.queryTrie(session, currentPrefix);
    }
}
