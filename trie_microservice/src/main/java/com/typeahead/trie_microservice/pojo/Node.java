package com.typeahead.trie_microservice.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    List<String> mostPopularWordsWithPrefix;
    boolean isEndOfWord;
    Map<Character,Node> map;

    public Node(){
        this.isEndOfWord = false;
        this.map = new HashMap<>();
        this.mostPopularWordsWithPrefix = new ArrayList<>();
    }
}
