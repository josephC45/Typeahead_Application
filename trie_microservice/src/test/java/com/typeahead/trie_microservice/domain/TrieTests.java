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

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

        Mono<List<String>> prefixesWithH = trie.getPrefixes("h");
        Mono<List<String>> prefixesWithT = trie.getPrefixes("testing");

        StepVerifier.create(prefixesWithH)
                .expectNextMatches(popularWordsWithPrefixH -> {
                    assertEquals(1, popularWordsWithPrefixH.size());
                    assertEquals("hello", popularWordsWithPrefixH.get(0));
                    return true;
                }).verifyComplete();

        StepVerifier.create(prefixesWithT).expectComplete().verify();
    }

    @Test
    public void givenTrieWithPrefixes_whenGetPrefixesIsCalled_shouldReturnMatchingPrefixes() {
        trie.addPrefix("don't");
        trie.addPrefix("dunk");
        trie.addPrefix("donut");
        trie.addPrefix("testing");

        Mono<List<String>> prefixesWithT = trie.getPrefixes("t");
        Mono<List<String>> prefixesWithD = trie.getPrefixes("d");
        Mono<List<String>> prefixesWithDont = trie.getPrefixes("don'");

        StepVerifier.create(prefixesWithT)
                .expectNextMatches(popularWordsWithPrefixT -> {
                    assertEquals(1, popularWordsWithPrefixT.size());
                    assertEquals("testing", popularWordsWithPrefixT.get(0));
                    return true;
                }).verifyComplete();

        StepVerifier.create(prefixesWithD)
                .expectNextMatches(popularWordsWithPrefixD -> {
                    assertEquals(3, popularWordsWithPrefixD.size());
                    assertTrue(popularWordsWithPrefixD.containsAll(List.of("don't", "donut")));
                    assertFalse(popularWordsWithPrefixD.contains("testing"));
                    return true;
                }).verifyComplete();

        StepVerifier.create(prefixesWithDont)
                .expectNextMatches(popularWordsWithPrefixDont -> {
                    assertEquals(1, popularWordsWithPrefixDont.size());
                    assertEquals("don't", popularWordsWithPrefixDont.get(0));
                    return true;
                }).verifyComplete();
    }
}
