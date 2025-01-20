package com.typeahead.trie_microservice.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.typeahead.trie_microservice.domain.TrieService;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

@ExtendWith(MockitoExtension.class)
public class WebsocketServiceImplTests {

    @Mock
    private TrieService trieService;

    @Mock
    private KafkaProducerService kafkaService;

    @InjectMocks
    private WebsocketServiceImpl websocketService;

    @Test
    void givenNonExistentPrefix_whenWebsocketQueriesTrie_shouldReturnPopularPrefixesToClient() throws IOException {
        String currentPrefix = "a";
        Mono<List<String>> mockPrefixes = Mono.just(Arrays.asList());
        when(trieService.getPopularPrefixes("a")).thenReturn(mockPrefixes);
        StringBuilder wordTyped = new StringBuilder();

        // Simulate typing "a"
        StepVerifier.create(
            websocketService.queryTrie(currentPrefix)
                .contextWrite(Context.of("wordTyped", wordTyped))
        )
        .expectNext("No popular prefixes")
        .verifyComplete();

        assertEquals(1, wordTyped.length());
        verify(trieService).getPopularPrefixes("a");
    }

    @Test
    void givenValidPrefix_whenWebsocketQueriesTrie_shouldReturnPopularPrefixesToClient() throws IOException {
        String currentPrefix = "t";
        Mono<List<String>> mockPrefixes = Mono.just(Arrays.asList("testing", "testosterone"));
        lenient().when(trieService.getPopularPrefixes("test")).thenReturn(mockPrefixes);
        StringBuilder wordTyped = new StringBuilder("tes");

        // Simulate typing final 't' in "test"
        StepVerifier.create(
            websocketService.queryTrie(currentPrefix)
                .contextWrite(Context.of("wordTyped", wordTyped))
        )
        .expectNext("testing,testosterone")
        .verifyComplete();

        assertEquals(4, wordTyped.length());
        verify(trieService).getPopularPrefixes("test");
    }

    @Test
    void whenEndOfWordCharacterIsTyped_shouldSendWordToKafka() throws Exception {
        String currentPrefix = "";
        StringBuilder wordTyped = new StringBuilder("test");
        when(kafkaService.sendMessageToKafka("test")).thenReturn(Mono.empty());

        // Simulate typing " "
        StepVerifier.create(
            websocketService.queryTrie(currentPrefix)
                .contextWrite(Context.of("wordTyped", wordTyped))
        )
        .expectNext("Word sent to Kafka")
        .verifyComplete();

        verify(kafkaService).sendMessageToKafka("test");
        assertEquals(0, wordTyped.length());
    }

}
