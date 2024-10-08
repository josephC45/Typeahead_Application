package com.typeahead.trie_microservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.domain.TrieInterface;

@Service
public class TrieService {

    private TrieInterface trie;
    
    public TrieService(TrieInterface trie){
        this.trie = trie;
    }

    public List<String> getPopularPrefixes(String prefix){
        return trie.getPrefix(prefix);
    }

    public void addCurPrefix(String prefix){
        trie.addPrefix(prefix);
    }
}
