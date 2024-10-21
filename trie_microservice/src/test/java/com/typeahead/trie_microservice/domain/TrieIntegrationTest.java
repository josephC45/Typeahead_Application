package com.typeahead.trie_microservice.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.typeahead.trie_microservice.service.TrieServiceImpl;

public class TrieIntegrationTest {

    @Test
    void givenPrefix_whenAdded_shouldReturnMatchingPrefixes(){

        Trie trie = new Trie();
        TrieServiceImpl trieService = new TrieServiceImpl(trie);

        String expected = "hello";
        trieService.addCurrentPrefix(expected);
        List<String> actual = trieService.getPopularPrefixes(expected);

        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }
}
