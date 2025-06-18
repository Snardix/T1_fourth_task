package com.example.t3.kafka;

import com.example.t3.model.TransactionMessage;
import com.example.t3.service.TransactionCheckerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionKafkaListener {

    private final TransactionCheckerService service;

    public TransactionKafkaListener(TransactionCheckerService service) {
        this.service = service;
    }

    @KafkaListener(topics = "t1_demo_transaction_accept", groupId = "transaction-checker-group")
    public void consume(TransactionMessage message) {
        service.handleTransaction(message);
    }
}