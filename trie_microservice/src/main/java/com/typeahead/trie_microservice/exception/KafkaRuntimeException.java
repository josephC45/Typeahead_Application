package com.typeahead.trie_microservice.exception;

public class KafkaRuntimeException extends RuntimeException {

    public KafkaRuntimeException(String message){
        super(message);
    }

    public KafkaRuntimeException(String message, Throwable cause){
        super(message, cause);
    }
}
