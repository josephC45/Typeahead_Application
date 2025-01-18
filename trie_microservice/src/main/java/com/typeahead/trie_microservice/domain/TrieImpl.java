package com.typeahead.trie_microservice.domain;

import java.util.Map;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.typeahead.trie_microservice.exception.TrieException;

import reactor.core.publisher.Flux;

@Component
public class TrieImpl implements TrieInterface {

    private final TrieNode rootNode;
    private static final Logger logger = LogManager.getLogger(TrieImpl.class);

    public TrieImpl() {
        this.rootNode = new TrieNode();
    }

    private Flux<String> dfsConstructPopularWords(TrieNode node, String prefix) {
        return Flux.create(sink -> {
            Stack<Map.Entry<TrieNode, String>> stack = new Stack<>();
            stack.push(Map.entry(node, prefix));
            while (!stack.isEmpty()) {
                Map.Entry<TrieNode, String> current = stack.pop();
                TrieNode currentNode = current.getKey();
                String currentPrefix = current.getValue();

                if (currentNode.isEndOfWord())
                    sink.next(currentPrefix);

                currentNode.getChildren()
                        .entrySet()
                        .forEach(child -> stack.push(Map.entry(child.getValue(), currentPrefix + child.getKey())));
            }

            sink.complete();
        });
    }

    @Override
    public Flux<String> getPrefixes(String prefix) {
        TrieNode currentNode = rootNode;
        for (char currentCharacter : prefix.toCharArray()) {
            currentNode = currentNode.getChildren().get(currentCharacter);
            if (currentNode == null)
                return Flux.empty();
        }
        return dfsConstructPopularWords(currentNode, prefix);
    }

    // Error handling put in place is temporary until HBase is
    // implemented and I determine how I need to handle it
    @Override
    public void addPrefix(String prefix) {
        try {
            if (prefix == null || prefix.isEmpty()) {
                throw new TrieException("Prefix cannot be null or empty.");
            }

            TrieNode currentNode = rootNode;
            for (char currentCharacter : prefix.toCharArray()) {
                currentNode = currentNode.addChildIfAbsent(currentCharacter);
            }
            currentNode.setEndOfWord(true);

        } catch (TrieException e) {
            logger.error("Error while adding prefixes: {}", e.getMessage());
            throw e; // revisit how we are testing/handling this exception
        }
    }

}
