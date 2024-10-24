package com.typeahead.trie_microservice.domain;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.typeahead.trie_microservice.exception.TrieRuntimeException;

@Component
public class Trie implements TrieInterface {

    private final Node rootNode; 
    private static final Logger logger = LogManager.getLogger(Trie.class);

    public Trie() {
        rootNode = new Node();
    }

    public List<String> getPrefixes(String prefix) {
        if(prefix == null || prefix.isEmpty()) {
            logger.warn("Attempted to get prefixes with a null or empty string.");
            throw new TrieRuntimeException("Prefix cannot be null or empty.");
        }
        
        Node currentNode = rootNode;
        for(char currentCharacter : prefix.toCharArray()) {
            currentNode = currentNode.getChildren().get(currentCharacter);
            if(currentNode == null) return Collections.emptyList();
        }
        return (currentNode.getMostPopularWordsWithPrefix() != null)
        ? currentNode.getMostPopularWordsWithPrefix() 
        : Collections.emptyList();        
    }

    public void addPrefix(String prefix) {
        try {
            if(prefix == null || prefix.isEmpty()) {
                throw new TrieRuntimeException("Prefix cannot be null or empty.");
            }

            Node currentNode = rootNode;
            for(char currentCharacter : prefix.toCharArray()) {
                currentNode = currentNode.addChildIfAbsent(currentCharacter);
                currentNode.addToPopularWords(prefix);
            }
            currentNode.setEndOfWord(true);

        } catch(TrieRuntimeException e){
            logger.error("Error while adding prefixes: {}", e.getMessage(), e);
        }
    }
}
