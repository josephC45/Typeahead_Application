package com.typeahead.trie_microservice.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.domain.Trie;
import com.typeahead.trie_microservice.exception.KafkaException;
import com.typeahead.trie_microservice.infrastructure.ConsumerService;

import reactor.core.publisher.Mono;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    private final Trie trie;
    private final ConsumerService kafkaService;
    private static final Logger logger = LogManager.getLogger(WebsocketServiceImpl.class);

    public WebsocketServiceImpl(@Qualifier("trieServiceImpl") Trie trie, ConsumerService kafkaService) {
        this.trie = trie;
        this.kafkaService = kafkaService;
    }

    private Mono<String> handleKafkaException(Throwable throwable) {
        logger.warn("Error occurred in sendMessageToKafka: {}", throwable.getMessage(), throwable);
        return Mono.just("Word could not be processed...");
    }

    private Mono<Boolean> isEndOfWord(String currentPrefix) {
        return Mono.just(currentPrefix.isEmpty() || currentPrefix.matches("[^A-Za-z']"));
    }

    @Override
    public Mono<String> queryTrie(String currentPrefix) {
        return Mono.deferContextual(context -> {
            StringBuilder wordTyped = context.get("wordTyped");
            return isEndOfWord(currentPrefix)
                    .flatMap(endOfWord -> {
                        if (!endOfWord) {
                            wordTyped.append(currentPrefix);
                            String curWordTyped = wordTyped.toString();

                            return trie.getPrefixes(curWordTyped)
                                    .map(popularWordsList -> {
                                        return (popularWordsList.isEmpty())
                                                ? "No popular prefixes"
                                                : String.join(",", popularWordsList);
                                    });
                        } else if (endOfWord && !wordTyped.isEmpty()) {
                            logger.info("End of word character reached. Sending to Kafka...");
                            kafkaService.sendMessageToConsumer(wordTyped.toString());
                            wordTyped.setLength(0);
                            return Mono.just("Word sent to Kafka");
                        } else
                            return Mono.just("Something unexpected happened");
                    }).onErrorResume(KafkaException.class, this::handleKafkaException);
        });
    }
}
