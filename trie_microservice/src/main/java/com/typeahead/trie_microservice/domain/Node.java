package com.typeahead.trie_microservice.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    private final List<String> mostPopularWordsWithPrefix;
    private final Map<Character, Node> children;
    private boolean isEndOfWord;

    public Node() {
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

    Map<Character, Node> getChildren() {
        return children;
    }

    Node addChildIfAbsent(char character) {
        return children.computeIfAbsent(character, c -> new Node());
    }

}
