package com.typeahead.trie_microservice.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

//TODO Revisit logging and determine where to place log statements
@Component
public class WebsocketHandler implements WebSocketHandler {

    private final WebsocketService websocketService;
    private static final Logger logger = LogManager.getLogger(WebsocketHandler.class);

    public WebsocketHandler(WebsocketService websocketService) {
        this.websocketService = websocketService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(currentPrefix -> {

                    StringBuilder wordTyped = new StringBuilder();

                    return Mono.deferContextual(context -> websocketService.queryTrie(currentPrefix))
                            .contextWrite(Context.of("wordTyped", wordTyped))
                            .flatMap(response -> session.send(Mono.just(session.textMessage(response))))
                            .then();
                })
                .then();
    }
}
