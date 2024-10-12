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
            if(!currentNode.map.containsKey(currentCharacter)) return Collections.emptyList();
            else currentNode = currentNode.map.get(currentCharacter);
        }
        return (currentNode.mostPopularWordsWithPrefix != null) ? currentNode.mostPopularWordsWithPrefix : Collections.emptyList();
    }

    public void addPrefix(String prefix){
        Node currentNode = rootNode;
        for(char currentCharacter : prefix.toCharArray()){
            if(!currentNode.map.containsKey(currentCharacter)){
                currentNode.mostPopularWordsWithPrefix.add(prefix);
                currentNode.map.put(currentCharacter, new Node());
            }
            currentNode = currentNode.map.get(currentCharacter);
        }
        currentNode.isEndOfWord = true;
    }
}
