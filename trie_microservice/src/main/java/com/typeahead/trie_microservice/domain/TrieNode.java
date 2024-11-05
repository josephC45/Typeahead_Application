package com.typeahead.trie_microservice.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrieNode {

    private final List<String> mostPopularWordsWithPrefix;
    private final Map<Character, TrieNode> children;
    private boolean isEndOfWord;

    public TrieNode() {
        this.isEndOfWord = false;
        this.children = new HashMap<>();
        this.mostPopularWordsWithPrefix = new ArrayList<>();
    }

    List<String> getMostPopularWordsWithPrefix() {
        return mostPopularWordsWithPrefix;
    }

    void addToPopularWords(String prefix) {
        this.mostPopularWordsWithPrefix.add(prefix);
    }

    boolean isEndOfWord() {
        return isEndOfWord;
    }

    void setEndOfWord(boolean isEndOfWord) {
        this.isEndOfWord = isEndOfWord;
    }

    Map<Character, TrieNode> getChildren() {
        return children;
    }

    TrieNode addChildIfAbsent(char character) {
        return children.computeIfAbsent(character, c -> new TrieNode());
    }

}
