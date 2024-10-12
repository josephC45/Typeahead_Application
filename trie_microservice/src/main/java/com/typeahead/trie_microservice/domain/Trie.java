package com.typeahead.trie_microservice.domain;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Trie implements TrieInterface {

    private final Node rootNode; 

    public Trie(){
        rootNode = new Node();
    }

    public List<String> getPrefixes(String prefix){
        Node currentNode = rootNode;
        for(char currentCharacter : prefix.toCharArray()){
            currentNode = currentNode.getChildren().get(currentCharacter);
            if(currentNode == null) return Collections.emptyList();
        }
        return (currentNode.getMostPopularWordsWithPrefix() != null)
        ? currentNode.getMostPopularWordsWithPrefix() 
        : Collections.emptyList();
    }

    public void addPrefix(String prefix){
        Node currentNode = rootNode;
        for(char currentCharacter : prefix.toCharArray()){
            currentNode = currentNode.addChildIfAbsent(currentCharacter);
            currentNode.addToPopularWords(prefix);
        }
        currentNode.setEndOfWord(true);
    }
}
