package com.typeahead.trie_microservice.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.typeahead.trie_microservice.service.TrieService;

public class TrieIntegrationTest {

    @Test
    void givenPrefix_whenAdded_shouldReturnMatchingPrefixes(){

        Trie trie = new Trie();
        TrieService trieService = new TrieService(trie);

        String expected = "hello";
        trieService.addCurPrefix(expected);
        List<String> actual = trieService.getPopularPrefixes(expected);
        assertTrue(actual.contains(expected));
    }
}
