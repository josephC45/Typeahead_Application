package com.typeahead.trie_microservice.UnitTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.typeahead.trie_microservice.websocket.WebsocketHandler;
import com.typeahead.trie_microservice.websocket.WebsocketService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

@ExtendWith(MockitoExtension.class)
public class WebsocketHandlerTests {

    @Mock 
    private WebsocketService websocketService;

    @InjectMocks
    private WebsocketHandler websocketHandler;

    @Test
    void whenQueryTrieSucceeds_shouldSendResponseBackToSession(){
        WebSocketSession session = mock(WebSocketSession.class);
        WebSocketMessage mockMessage = mock(WebSocketMessage.class);

        when(session.receive()).thenReturn(Flux.just(mockMessage));
        when(mockMessage.getPayloadAsText()).thenReturn("t");

        when(session.textMessage("tester,testing")).thenReturn(mock(WebSocketMessage.class));
        when(session.send(any())).thenReturn(Mono.empty());
        
        when(websocketService.queryTrie("t"))
            .thenReturn(Mono.deferContextual(context -> {
                StringBuilder wordTyped = context.get("wordTyped");
                wordTyped.append("t");
                return Mono.just("tester,testing");
            }));

        Mono<Void> result = websocketHandler.handle(session)
            .contextWrite(Context.of("wordTyped", new StringBuilder()));

        StepVerifier.create(result).verifyComplete();
        verify(session).send(any());
    }

    @Test
    void whenQueryTrieSucceedsWithEndOfWord_shouldSendResponseBackToSession(){
        WebSocketSession session = mock(WebSocketSession.class);
        WebSocketMessage mockMessage = mock(WebSocketMessage.class);

        when(session.receive()).thenReturn(Flux.just(mockMessage));
        when(mockMessage.getPayloadAsText()).thenReturn(" ");

        when(session.textMessage("Word sent to Kafka")).thenReturn(mock(WebSocketMessage.class));
        when(session.send(any())).thenReturn(Mono.empty());
        
        when(websocketService.queryTrie(""))
            .thenReturn(Mono.deferContextual(context -> {
                return Mono.just("Word sent to Kafka");
            }));

        Mono<Void> result = websocketHandler.handle(session)
            .contextWrite(Context.of("wordTyped", new StringBuilder("test")));

        StepVerifier.create(result).verifyComplete();
        verify(session).send(any());
    }

    @Test
    void whenQueryTrieTimesOut_shouldSendResponseBackToSession(){
        WebSocketSession session = mock(WebSocketSession.class);
        WebSocketMessage mockMessage = mock(WebSocketMessage.class);

        when(session.receive()).thenReturn(Flux.just(mockMessage));
        when(mockMessage.getPayloadAsText()).thenReturn("t");

        when(session.textMessage("Error: Query timed out.")).thenReturn(mock(WebSocketMessage.class));
        when(session.send(any())).thenReturn(Mono.empty());
        
        when(websocketService.queryTrie("t"))
            .thenReturn(Mono.delay(Duration.ofSeconds(4))
            .then(Mono.just("")));

        Mono<Void> result = websocketHandler.handle(session)
            .contextWrite(Context.of("wordTyped", new StringBuilder()));

        StepVerifier.create(result).verifyComplete();
        verify(session).send(any());
    }
}
