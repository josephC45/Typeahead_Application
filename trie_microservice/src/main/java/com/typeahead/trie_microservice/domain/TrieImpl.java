package com.typeahead.trie_microservice.domain;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.typeahead.trie_microservice.exception.TrieException;

@Component
public class TrieImpl implements TrieInterface {

    private final TrieNode rootNode;
    private final Logger logger;

    public TrieImpl() {
        this.rootNode = new TrieNode();
        this.logger = LogManager.getLogger(TrieImpl.class);
    }

    public List<String> getPrefixes(String prefix) {
        TrieNode currentNode = rootNode;
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
                throw new TrieException("Prefix cannot be null or empty.");
            }

            TrieNode currentNode = rootNode;
            for (char currentCharacter : prefix.toCharArray()) {
                currentNode = currentNode.addChildIfAbsent(currentCharacter);
                currentNode.addToPopularWords(prefix);
            }
            currentNode.setEndOfWord(true);

        } catch (TrieException e) {
            logger.error("Error while adding prefixes: {}", e.getMessage());
            throw e; //revisit how we are testing/handling this exception
        }
    }
}
