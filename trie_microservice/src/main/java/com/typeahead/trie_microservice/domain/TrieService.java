package com.typeahead.trie_microservice.domain;

import java.util.List;

public interface TrieService {

    public List<String> getPopularPrefixes(String prefix);

    public void addCurrentPrefix(String prefix); 
    
}
