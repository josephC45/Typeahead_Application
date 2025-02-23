package com.typeahead.trie_microservice.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.typeahead.trie_microservice.domain.Trie;
import com.typeahead.trie_microservice.service.TrieServiceImpl;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class TrieServiceTests {

    @Mock
    private Trie trieMock;

    @InjectMocks
    private TrieServiceImpl trieService;

    @Test
    public void givenExistingTrie_whenPopularPrefixesIsCalled_shouldCallGetPrefixesOnTrie() {
        String prefix = "h";
        Mono<List<String>> expectedPrefixes = Mono.just(Arrays.asList("hello", "hey"));

        when(trieMock.getPrefixes(prefix)).thenReturn(expectedPrefixes);

        Mono<List<String>> result = trieService.getPrefixes(prefix);
        assertEquals(expectedPrefixes, result);
        verify(trieMock).getPrefixes(prefix);
    }

    @Test
    public void givenPrefix_whenAddedToTrie_shouldCallAddPrefixOnTrie() {
        String prefix = "hello";
        trieService.addPrefix(prefix);
        verify(trieMock).addPrefix(prefix);
    }
}
