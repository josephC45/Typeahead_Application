package com.typeahead.trie_microservice.infrastructure;

public interface KafkaInterface {
    public void sendPrefix(String prefix);
}
