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
    private final Logger logger;

    public Trie() {
        this.rootNode = new Node();
        this.logger = LogManager.getLogger(Trie.class);
    }

    public List<String> getPrefixes(String prefix) {
        Node currentNode = rootNode;
        for (char currentCharacter : prefix.toCharArray()) {
            currentNode = currentNode.getChildren().get(currentCharacter);
            if (currentNode == null)
                return Collections.emptyList();
        }
        return (currentNode.getMostPopularWordsWithPrefix() != null)
                ? currentNode.getMostPopularWordsWithPrefix()
                : Collections.emptyList();
    }

    // Error handling put in place is temporary until HBase is
    // implemented and I determine how I need to handle it
    public void addPrefix(String prefix) {
        try {
            if (prefix == null || prefix.isEmpty()) {
                throw new TrieRuntimeException("Prefix cannot be null or empty.");
            }

            Node currentNode = rootNode;
            for (char currentCharacter : prefix.toCharArray()) {
                currentNode = currentNode.addChildIfAbsent(currentCharacter);
                currentNode.addToPopularWords(prefix);
            }
            currentNode.setEndOfWord(true);

        } catch (TrieRuntimeException e) {
            logger.error("Error while adding prefixes: {}", e.getMessage());
            throw e; //revisit how we are testing/handling this exception
        }
    }
}
