package com.typeahead.trie_microservice.websocket;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class WebsocketHandler extends AbstractWebSocketHandler {

    private final WebsocketService websocketService;
    private static final Logger logger = LogManager.getLogger(WebsocketHandler.class);

    public WebsocketHandler(WebsocketService websocketService) {
        this.websocketService = websocketService;
    }

    private Mono<Void> handleTimeoutException(WebSocketSession session, Throwable throwable) {
        logger.warn("WebSocket query timed out: {}", throwable.getMessage());
        return session.send(Mono.just(session.textMessage("Error: Query timed out.")));
    }

    private Mono<Void> handleIOException(WebSocketSession session, Throwable throwable) {
        logger.error("I/O error during WebSocket communication: {}", throwable.getMessage());
        return session.close(CloseStatus.SERVER_ERROR);
    }

    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(currentPrefix -> {

                    StringBuilder wordTyped = new StringBuilder();
                    return Mono.deferContextual(context -> websocketService.queryTrie(currentPrefix.stripLeading()))
                            .timeout(Duration.ofSeconds(3))
                            .contextWrite(Context.of("wordTyped", wordTyped))
                            .flatMap(response -> session.send(Mono.just(session.textMessage(response))))
                            .then();
                })
                .onErrorResume(TimeoutException.class, toe -> handleTimeoutException(session, toe))
                .onErrorResume(IOException.class, ioe -> handleIOException(session, ioe))
                .then();
    }
}
