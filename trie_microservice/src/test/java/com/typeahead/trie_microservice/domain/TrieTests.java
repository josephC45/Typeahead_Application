package com.typeahead.trie_microservice.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TrieTests {

    @Test
    public void givenPrefix_whenAdded_shouldReturnMatchingPrefixes() {
        Trie trie = new Trie();
        trie.addPrefix("hello");
        List<String> result = trie.getPrefixes("h");
        assertTrue(result.contains("hello"));
        assertTrue(trie.getPrefixes("testing").isEmpty());
    }

    @Test
    public void givenTrieWithPrefixes_whenGetPrefixesIsCalled_shouldReturnMatchingPrefixes() {
        Trie trie = new Trie();
        trie.addPrefix("hello");
        trie.addPrefix("howdy");
        trie.addPrefix("testing");
        
        List<String> result = trie.getPrefixes("h");
        assertTrue(result.contains("hello"));
        assertTrue(result.contains("howdy"));
        assertFalse(result.contains("testing"));
    }
}
