package com.typeahead.trie_microservice.domain;

import java.util.List;

import reactor.core.publisher.Mono;

public interface TrieService {

    public Mono<List<String>> getPopularPrefixes(String prefix);

    public void addCurrentPrefix(String prefix);

}
