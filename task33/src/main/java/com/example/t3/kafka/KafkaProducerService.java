package com.example.t3.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "t1_demo_metrics";

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String errorType, String payload) {
        var message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("errorType", errorType)
                .build();

        kafkaTemplate.send(message);
    }
}
