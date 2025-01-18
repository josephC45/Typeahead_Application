package com.typeahead.trie_microservice.infrastructure;

import reactor.core.publisher.Mono;

public interface KafkaProducerService {

    public Mono<Void> sendMessageToKafka(String prefix);

}
