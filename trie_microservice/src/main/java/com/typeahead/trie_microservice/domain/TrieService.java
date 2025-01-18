package com.typeahead.trie_microservice.domain;

import reactor.core.publisher.Flux;

public interface TrieService {

    public Flux<String> getPopularPrefixes(String prefix);

    public void addCurrentPrefix(String prefix); 
    
}
