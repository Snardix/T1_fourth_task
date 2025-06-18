package com.example.t3.kafka;

import com.example.t3.model.TransactionMessage;
import com.example.t3.service.TransactionCheckerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionCheckerService checkerService;

    public TransactionListener(TransactionCheckerService checkerService) {
        this.checkerService = checkerService;
    }

    @KafkaListener(topics = "t1_demo_transaction_accept", groupId = "t3-checker")
    public void listen(TransactionMessage message) {
        checkerService.handleTransaction(message);
    }
}