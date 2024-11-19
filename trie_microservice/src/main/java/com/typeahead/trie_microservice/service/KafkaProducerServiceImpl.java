package com.typeahead.trie_microservice.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.typeahead.trie_microservice.exception.KafkaException;
import com.typeahead.trie_microservice.infrastructure.KafkaProducerService;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Tracer tracer;
    private static final Logger logger = LogManager.getLogger(KafkaProducerServiceImpl.class);

    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendMessageToKafka(String prefix) {
        Span span = tracer.nextSpan().name("kafka-producer").start();
        try {
            tracer.withSpan(span);
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                ProducerRecord<String, String> record = createKafkaRecord(prefix, currentSpan);
                kafkaTemplate.send(record);
            } else {
                logger.warn("No active trace context found, sending message without tracing.");
                kafkaTemplate.sendDefault(prefix);
            }
        } catch (KafkaException e) {
            logger.error("KafkaTemplate sendDefault ran into error sending prefix to Kafka: {}", e.getMessage(), e);
            throw e;
        } finally {
            span.end();
        }
    }

    private ProducerRecord<String, String> createKafkaRecord(String prefix, Span currentSpan) {
        ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTemplate.getDefaultTopic(), prefix);

        record.headers().add("trace-id", currentSpan.context().traceId().getBytes());
        record.headers().add("span-id", currentSpan.context().spanId().getBytes());
        record.headers().add("parent-span-id", currentSpan.context().parentId().getBytes());

        return record;
    }

}
