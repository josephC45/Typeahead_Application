package com.typeahead.trie_microservice.domain;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private final Map<Character, TrieNode> children;
    private boolean isEndOfWord;

    public TrieNode() {
        this.isEndOfWord = false;
        this.children = new HashMap<>();
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
