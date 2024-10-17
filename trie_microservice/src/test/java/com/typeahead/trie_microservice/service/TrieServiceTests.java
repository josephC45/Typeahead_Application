package com.typeahead.trie_microservice.service;

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

import com.typeahead.trie_microservice.domain.TrieInterface;

@ExtendWith(MockitoExtension.class)
public class TrieServiceTests {

    @Mock
    private TrieInterface trieMock;

    @InjectMocks
    private TrieService trieService;

    @Test
    public void givenExistingTrie_whenPopularPrefixesIsCalled_shouldCallGetPrefixesOnTrie() {
        String prefix = "h";
        List<String> expectedPrefixes = Arrays.asList("hello", "hey");
        
        when(trieMock.getPrefixes(prefix)).thenReturn(expectedPrefixes);
        
        List<String> result = trieService.getPopularPrefixes(prefix);
        assertEquals(expectedPrefixes, result);
        verify(trieMock).getPrefixes(prefix);
    }

    @Test
    public void givenPrefix_whenAddedToTrie_shouldCallAddPrefixOnTrie() {
        String prefix = "hello";
        
        trieService.addCurPrefix(prefix);
        verify(trieMock).addPrefix(prefix);
    }
}
