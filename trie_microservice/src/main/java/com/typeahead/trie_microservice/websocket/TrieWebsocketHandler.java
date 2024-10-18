package com.typeahead.trie_microservice.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class TrieWebsocketHandler extends TextWebSocketHandler {

    private final WebsocketService websocketService;

    public TrieWebsocketHandler(WebsocketService websocketService){
        this.websocketService = websocketService;
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

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage){
        String currentPrefix = textMessage.getPayload();
        System.out.println("Received message: " + currentPrefix + " from client: " + session.getId());
        websocketService.queryTrie(session, currentPrefix);
    }
}
