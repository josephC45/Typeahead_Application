package com.typeahead.trie_microservice.infrastructure;

public interface KafkaProducerService {

    public void sendMessageToKafka(String prefix);
    
}
