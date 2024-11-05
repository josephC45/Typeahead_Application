package com.typeahead.trie_microservice.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.typeahead.trie_microservice.exception.TrieException;

public class TrieTests {

    private TrieImpl trie;

    @Autowired
    private TrieImpl trieSingleton2;

    @Autowired
    private TrieImpl trieSingleton1;

    @BeforeEach
    public void setup() {
        trie = new TrieImpl();
    }

    @Test
    public void testSingletonInstance() {
        assertSame(trieSingleton1, trieSingleton2);
    }

    @Test
    public void givenEmptyPrefix_whenAdded_shouldThrowTrieExceptionAndLog() {
        TrieException exception = assertThrows(TrieException.class, () -> trie.addPrefix(""));
        assertEquals("Prefix cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void givenNullPrefix_whenAdded_shouldThrowTrieExceptionAndLog() {
        TrieException exception = assertThrows(TrieException.class, () -> trie.addPrefix(null));
        assertEquals("Prefix cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void givenPrefix_whenAdded_shouldReturnMatchingPrefixes() {
        trie.addPrefix("hello");

        List<String> prefixesWithH = trie.getPrefixes("h");
        List<String> prefixesWithT = trie.getPrefixes("testing");

        assertEquals(1, prefixesWithH.size());
        assertTrue(prefixesWithH.contains("hello"));
        assertEquals(0, prefixesWithT.size());
    }

    @Test
    public void givenTrieWithPrefixes_whenGetPrefixesIsCalled_shouldReturnMatchingPrefixes() {
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
