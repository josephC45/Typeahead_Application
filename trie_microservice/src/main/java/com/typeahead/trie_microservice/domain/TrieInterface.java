package com.typeahead.trie_microservice.domain;

import java.util.List;

import reactor.core.publisher.Mono;

public interface TrieInterface {

    public Mono<List<String>> getPrefixes(String prefix);

    public void addPrefix(String prefix); 
    
}
