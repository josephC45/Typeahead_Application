package com.typeahead.trie_microservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.exception.KafkaException;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;

import reactor.core.publisher.Mono;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(KafkaProducerServiceImpl.class);

    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private Throwable handleKafkaException(Throwable throwable) {
        logger.error("Failed to send message to Kafka: {}", throwable.getMessage(), throwable);
        return new KafkaException("Kafka message sending failed", throwable);
    }

    public Mono<Void> sendMessageToKafka(String prefix) {
        return Mono.fromRunnable(() -> kafkaTemplate.sendDefault(prefix))
                .onErrorMap(KafkaException.class, this::handleKafkaException)
                .doOnTerminate(() -> logger.info("Message sent to Kafka: {} ", prefix))
                .then();
    }
}
