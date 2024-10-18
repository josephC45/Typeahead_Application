package com.typeahead.trie_microservice.domain;

import java.util.List;

public interface TrieInterface {

    public List<String> getPrefixes(String prefix);

    public void addPrefix(String prefix); 
    
}
