package com.typeahead.trie_microservice.domain;

import reactor.core.publisher.Flux;

public interface TrieInterface {

    public Flux<String> getPrefixes(String prefix);

    public void addPrefix(String prefix); 
    
}
