package com.typeahead.trie_microservice.exception;

public class TrieException extends RuntimeException {

    public TrieException(String message) {
        super(message);
    }

    public TrieException(String message, Throwable cause) {
        super(message, cause);
    }
}
