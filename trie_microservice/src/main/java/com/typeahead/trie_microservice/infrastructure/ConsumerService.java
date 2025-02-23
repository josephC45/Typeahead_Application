package com.typeahead.trie_microservice.infrastructure;

import reactor.core.publisher.Mono;

public interface ConsumerService {

    public Mono<Void> sendMessageToConsumer(String prefix);

}
