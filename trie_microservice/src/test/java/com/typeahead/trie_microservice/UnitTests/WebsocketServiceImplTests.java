package com.typeahead.trie_microservice.UnitTests;

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

import com.typeahead.trie_microservice.domain.Trie;
import com.typeahead.trie_microservice.exception.KafkaException;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;
import com.typeahead.trie_microservice.websocket.WebsocketServiceImpl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

@ExtendWith(MockitoExtension.class)
public class WebsocketServiceImplTests {

        @Mock
        private Trie trieService;

        @Mock
        private KafkaProducerService kafkaService;

        @InjectMocks
        private WebsocketServiceImpl websocketService;

        @Test
        void givenNonExistentPrefix_whenWebsocketQueriesTrie_shouldReturnPopularPrefixesToClient() throws IOException {
                String currentPrefix = "a";
                Mono<List<String>> mockPrefixes = Mono.just(Arrays.asList());
                StringBuilder wordTyped = new StringBuilder();
                when(trieService.getPrefixes("a")).thenReturn(mockPrefixes);

                // Simulate typing "a"
                StepVerifier.create(
                                websocketService.queryTrie(currentPrefix)
                                                .contextWrite(Context.of("wordTyped", wordTyped)))
                                .expectNext("No popular prefixes")
                                .verifyComplete();

                assertEquals(1, wordTyped.length());
                verify(trieService).getPrefixes("a");
        }

        @Test
        void givenValidPrefix_whenWebsocketQueriesTrie_shouldReturnPopularPrefixesToClient() throws IOException {
                String currentPrefix = "t";
                Mono<List<String>> mockPrefixes = Mono.just(Arrays.asList("testing", "testosterone"));
                StringBuilder wordTyped = new StringBuilder("tes");
                lenient().when(trieService.getPrefixes("test")).thenReturn(mockPrefixes);

                // Simulate typing final 't' in "test"
                StepVerifier.create(
                                websocketService.queryTrie(currentPrefix)
                                                .contextWrite(Context.of("wordTyped", wordTyped)))
                                .expectNext("testing,testosterone")
                                .verifyComplete();

                assertEquals(4, wordTyped.length());
                verify(trieService).getPrefixes("test");
        }

        @Test
        void whenEndOfWordCharacterIsTyped_shouldSendWordToKafka() throws Exception {
                String currentPrefix = "";
                StringBuilder wordTyped = new StringBuilder("test");
                when(kafkaService.sendMessageToKafka("test")).thenReturn(Mono.empty());

                // Simulate typing " "
                StepVerifier.create(
                                websocketService.queryTrie(currentPrefix)
                                                .contextWrite(Context.of("wordTyped", wordTyped)))
                                .expectNext("Word sent to Kafka")
                                .verifyComplete();

                verify(kafkaService).sendMessageToKafka("test");
                assertEquals(0, wordTyped.length());
        }

        @Test
        void whenSendMessageToKafkaThrowsKafkaException_shouldPropagateNotProcessedMessageToClient() {
                String currentPrefix = "";
                StringBuilder wordTyped = new StringBuilder("test");
                when(kafkaService.sendMessageToKafka("test")).thenThrow(KafkaException.class);

                Mono<String> result = websocketService.queryTrie(currentPrefix)
                                .contextWrite(Context.of("wordTyped", wordTyped));

                StepVerifier.create(result)
                                .expectNext("Word could not be processed...")
                                .verifyComplete();

                verify(kafkaService).sendMessageToKafka("test");
        }

}
