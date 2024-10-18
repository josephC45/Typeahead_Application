package com.typeahead.trie_microservice.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public interface WebsocketService {
    
    public boolean isEndOfWord(String currentPrefix);

    public void sendResponseToClient(WebSocketSession session, TextMessage textMessage);

    public void queryTrie(WebSocketSession session, String currentPrefix);
    
}
