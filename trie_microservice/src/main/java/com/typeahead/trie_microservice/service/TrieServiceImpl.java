package com.typeahead.trie_microservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.domain.TrieInterface;
import com.typeahead.trie_microservice.domain.TrieService;

@Service
public class TrieServiceImpl implements TrieService {

    private final TrieInterface trie;

    public TrieServiceImpl(TrieInterface trie){
        this.trie = trie;
    }
    
    @Override
    public List<String> getPopularPrefixes(String prefix) {
        return trie.getPrefixes(prefix);
    }

    @Override
    public void addCurrentPrefix(String prefix){
        trie.addPrefix(prefix);
    }

}
