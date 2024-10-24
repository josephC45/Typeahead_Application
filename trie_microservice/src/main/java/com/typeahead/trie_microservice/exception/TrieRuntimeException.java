package com.typeahead.trie_microservice.exception;

public class TrieRuntimeException extends RuntimeException {

    public TrieRuntimeException(String message){
        super(message);
    }

    public TrieRuntimeException(String message, Throwable cause){
        super(message, cause);
    }
}
