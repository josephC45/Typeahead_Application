package com.typeahead.trie_microservice.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TrieTests {

    @Test
    public void givenPrefix_whenAdded_shouldReturnMatchingPrefixes() {
        Trie trie = new Trie();
        trie.addPrefix("hello");

        List<String> prefixesWithH = trie.getPrefixes("h");
        List<String> prefixesWithT = trie.getPrefixes("testing");

        assertTrue(prefixesWithH.contains("hello"));
        assertEquals(0, prefixesWithT.size());
    }

    @Test
    public void givenTrieWithPrefixes_whenGetPrefixesIsCalled_shouldReturnMatchingPrefixes() {
        Trie trie = new Trie();
        trie.addPrefix("don't");
        trie.addPrefix("dunk");
        trie.addPrefix("donut");
        trie.addPrefix("testing");

        List<String> prefixesWithT = trie.getPrefixes("t");
        List<String> prefixesWithD = trie.getPrefixes("d");
        List<String> prefixesWithDont = trie.getPrefixes("don'");

        assertEquals(3, prefixesWithD.size());
        assertTrue(prefixesWithD.containsAll(List.of("don't", "donut")));
        
        assertEquals(1, prefixesWithDont.size());
        assertTrue(prefixesWithDont.contains("don't"));

        assertTrue(prefixesWithT.contains("testing"));
        assertFalse(prefixesWithD.contains("testing"));
    }
}
