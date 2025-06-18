package com.example.t3.service;

import com.example.t3.config.TransactionLimitProperties;
import com.example.t3.model.TransactionMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class TransactionCheckerService {

    private final TransactionLimitProperties properties;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final Map<String, List<TransactionMessage>> transactionHistory = new HashMap<>();

    public TransactionCheckerService(TransactionLimitProperties properties, KafkaTemplate<String, Object> kafkaTemplate) {
        this.properties = properties;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void handleTransaction(TransactionMessage msg) {
        String key = msg.getClientId() + "_" + msg.getAccountId();
        transactionHistory.putIfAbsent(key, new ArrayList<>());
        List<TransactionMessage> list = transactionHistory.get(key);

        list.add(msg);
        list.removeIf(tx -> tx.getTimestamp().isBefore(Instant.now().minus(properties.getWindow())));

        if (list.size() >= properties.getCount()) {
            list.forEach(tx -> sendResult(tx, "BLOCKED"));
            list.clear(); // или оставить, зависит от логики
            return;
        }

        if (msg.getAmount() > msg.getBalance()) {
            sendResult(msg, "REJECTED");
        } else {
            sendResult(msg, "ACCEPTED");
        }
    }

    private void sendResult(TransactionMessage msg, String status) {
        Map<String, Object> result = new HashMap<>();
        result.put("transactionId", msg.getTransactionId());
        result.put("accountId", msg.getAccountId());
        result.put("status", status);
        kafkaTemplate.send("t1_demo_transaction_result", result);
    }
}