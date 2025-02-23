package com.typeahead.trie_microservice.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.typeahead.trie_microservice.domain.TrieImpl;
import com.typeahead.trie_microservice.service.TrieServiceImpl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TrieIntegrationTest {

    @Test
    void givenPrefix_whenAdded_shouldReturnMatchingPrefixes() {

        TrieImpl trieDataStructure = new TrieImpl();
        TrieServiceImpl trieService = new TrieServiceImpl(trieDataStructure);

        String expected = "hello";
        trieService.addPrefix(expected);
        Mono<List<String>> actual = trieService.getPrefixes(expected);

        StepVerifier.create(actual)
                .expectNextMatches(popularWordsList -> {
                    assertNotNull(popularWordsList);
                    assertEquals(expected, popularWordsList.get(0));
                    return true;
                }).verifyComplete();
    }
}
