package com.typeahead.trie_microservice.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.domain.TrieService;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;

import reactor.core.publisher.Mono;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    private final TrieService trieService;
    private final KafkaProducerService kafkaService;
    private static final Logger logger = LogManager.getLogger(WebsocketServiceImpl.class);

    public WebsocketServiceImpl(TrieService trieService, KafkaProducerService kafkaService) {
        this.trieService = trieService;
        this.kafkaService = kafkaService;
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

                            return trieService.getPopularPrefixes(curWordTyped)
                                    .map(popularWordsList -> {
                                        return (popularWordsList.isEmpty())
                                                ? "No popular prefixes"
                                                : String.join(",", popularWordsList);
                                    });
                        } else if (endOfWord && !wordTyped.isEmpty()) {
                            logger.info("End of word character reached. Sending to Kafka...");
                            kafkaService.sendMessageToKafka(wordTyped.toString());
                            wordTyped.setLength(0);
                            return Mono.just("Word sent to Kafka");
                        } else
                            return Mono.just("Something unexpected happened"); // TODO temp
                    });
        });
    }
}
