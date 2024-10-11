package com.typeahead.trie_microservice.service;

import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.infrastructure.KafkaInterface;

@Service
public class KafkaProducerService {

    private final KafkaInterface kafkaProducer;

    public KafkaProducerService(KafkaInterface kafkaProducer){
        this.kafkaProducer = kafkaProducer;
    }

    public void sendMessageToKafka(String prefix){
        kafkaProducer.sendPrefix(prefix);
    }
}
