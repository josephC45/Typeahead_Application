package com.typeahead.trie_microservice.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TrieTests {

    @Mock
    private Logger logger;

    @InjectMocks
    private Trie trie;

    @Test
    public void givenEmptyPrefix_whenAdded_shouldThrowTrieExceptionAndLog() {
        trie.addPrefix("");

        Mockito.verify(logger).error(eq("Error while adding prefixes: {}"), eq("Prefix cannot be null or empty."));
    }

    @Test
    public void givenNullPrefix_whenAdded_shouldThrowTrieExceptionAndLog() {
        trie.addPrefix(null);

        Mockito.verify(logger).error(eq("Error while adding prefixes: {}"), eq("Prefix cannot be null or empty."));
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
