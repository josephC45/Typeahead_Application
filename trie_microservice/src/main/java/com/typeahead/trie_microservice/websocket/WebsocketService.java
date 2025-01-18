package com.typeahead.trie_microservice.websocket;

import reactor.core.publisher.Mono;

public interface WebsocketService {

    public Mono<String> queryTrie(String currentPrefix);

}
