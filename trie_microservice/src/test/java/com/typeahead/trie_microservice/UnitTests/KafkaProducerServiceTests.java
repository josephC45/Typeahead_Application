package com.typeahead.trie_microservice.UnitTests;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.typeahead.trie_microservice.exception.KafkaException;
import com.typeahead.trie_microservice.service.KafkaProducerServiceImpl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTests {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplateMock;

    @InjectMocks
    private KafkaProducerServiceImpl kafkaService;

    @Test
    void whenSendMessageToKafkaCalled_shouldSendMessageSuccessfully() {
        String prefix = "test";
        when(kafkaTemplateMock.sendDefault(prefix)).thenReturn(null);

        Mono<Void> result = kafkaService.sendMessageToKafka(prefix);

        StepVerifier.create(result).verifyComplete();

        verify(kafkaTemplateMock, times(1)).sendDefault(prefix);
    }

    @Test
    void whenSendMessageToKafkaThrowsKafkaException_shouldThrowException() {
        String prefix = "test";
        when(kafkaTemplateMock.sendDefault(prefix)).thenThrow(KafkaException.class);

        Mono<Void> result = kafkaService.sendMessageToKafka(prefix);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof KafkaException
                        && throwable.getMessage().equals("Kafka message sending failed"))
                .verify();

        verify(kafkaTemplateMock, times(1)).sendDefault(prefix);
    }
}
