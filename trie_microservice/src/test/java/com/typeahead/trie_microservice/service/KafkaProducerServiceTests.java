package com.typeahead.trie_microservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.typeahead.trie_microservice.exception.KafkaException;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTests {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplateMock;

    @InjectMocks
    private KafkaProducerServiceImpl kafkaService;

    @Test
    void whenSendMessageToKafkaCalled_shouldSendMessageSuccessfully() {
        kafkaService.sendMessageToKafka("test");
        verify(kafkaTemplateMock, times(1)).sendDefault("test");
    }

    @Test
    void whenSendMessageToKafkaThrowsKafkaException_shouldThrowException() {
        doThrow(new KafkaException("Kafka exception occurred")).when(kafkaTemplateMock).sendDefault("test");
        assertThrows(KafkaException.class, () -> kafkaService.sendMessageToKafka("test"));
        verify(kafkaTemplateMock, times(1)).sendDefault("test");
    }
}
