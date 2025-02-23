package com.typeahead.trie_microservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.domain.Trie;
import reactor.core.publisher.Mono;

@Service
public class TrieServiceImpl implements Trie {

    private Trie trie;

    public TrieServiceImpl(@Qualifier("trieImpl") Trie trie) {
        this.trie = trie;
    }

    @Override
    public Mono<List<String>> getPrefixes(String prefix) {
        return trie.getPrefixes(prefix);
    }

    @Override
    public void addPrefix(String prefix) {
        trie.addPrefix(prefix);
    }

}
