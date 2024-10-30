package com.typeahead.trie_microservice.websocket;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.typeahead.trie_microservice.domain.TrieService;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;
import com.typeahead.trie_microservice.exception.KafkaRuntimeException;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    private final TrieService trieService;
    private final KafkaProducerService kafkaService;
    private final StringBuilder wordTyped = new StringBuilder();
    private static final Logger logger = LogManager.getLogger(WebsocketServiceImpl.class);

    public WebsocketServiceImpl(TrieService trieService, KafkaProducerService kafkaService) {
        this.trieService = trieService;
        this.kafkaService = kafkaService;
    }

    @Override
    public boolean isEndOfWord(String currentPrefix) {
        return currentPrefix.matches("[^A-Za-z']");
    }

    @Override
    public void sendResponseToClient(WebSocketSession session, TextMessage response) {
        try {
            session.sendMessage(response);
            logger.info("Response sent to client.");
        } catch (IOException e) {
            logger.error("Failed to send to websocket client: " + e.getMessage(), e);
        }
    }

    @Override
    public void queryTrie(WebSocketSession session, String currentPrefix) {
        try {
            if (!isEndOfWord(currentPrefix)) {
                wordTyped.append(currentPrefix);
                String curWordTyped = wordTyped.toString();
                logger.info("Querying trie with prefix: " + curWordTyped);
                List<String> popularAssociatedWordsWithPrefix = trieService.getPopularPrefixes(curWordTyped);

                TextMessage response = (popularAssociatedWordsWithPrefix.isEmpty())
                        ? new TextMessage("No popular prefixes")
                        : new TextMessage(String.join(",", popularAssociatedWordsWithPrefix));

                sendResponseToClient(session, response);
            } else {
                logger.info("End of word character reached. Sending to Kafka...");
                kafkaService.sendMessageToKafka(wordTyped.toString());
                wordTyped.setLength(0);
            }

        } catch (KafkaRuntimeException e){
            logger.error("Error sending prefix to Kafka: {}", e.getMessage(), e);
            sendResponseToClient(session, new TextMessage("Error sending data to Kafka."));
        }
    }
}
