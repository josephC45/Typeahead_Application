package com.typeahead.trie_microservice.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer implements KafkaInterface {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendPrefix(String prefix){
        kafkaTemplate.sendDefault(prefix);
    }
}
