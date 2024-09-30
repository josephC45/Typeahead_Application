package com.typeahead.trie_microservice.typeahead_trie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendPrefix(String prefix){
        kafkaTemplate.send("typed-prefixes", prefix);
    }
}
