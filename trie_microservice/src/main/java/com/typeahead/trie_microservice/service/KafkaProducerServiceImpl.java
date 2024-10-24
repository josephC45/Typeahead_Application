package com.typeahead.trie_microservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.exception.KafkaRuntimeException;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(KafkaProducerServiceImpl.class);

    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageToKafka(String prefix) {
        try {
            kafkaTemplate.sendDefault(prefix);
        } catch (KafkaRuntimeException e){
            logger.error("KafkaTemplate sendDefault ran into error sending prefix to Kafka: {}", e.getMessage(), e);
            throw e;
        }
    }

}
