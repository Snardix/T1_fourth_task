package com.example.t3.kafka;

import com.example.t3.model.*;
import com.example.t3.repository.AccountRepository;
import com.example.t3.repository.TransactionRepository;
import com.example.t3.service.ClientStatusService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionKafkaListener {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ClientStatusService clientStatusService;

    public TransactionKafkaListener(AccountRepository accountRepository,
                                    TransactionRepository transactionRepository,
                                    KafkaTemplate<String, String> kafkaTemplate,
                                    ClientStatusService clientStatusService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.clientStatusService = clientStatusService;
    }

    @KafkaListener(topics = "t1_demo_transactions", groupId = "transaction-group")
    @Transactional
    public void handleTransaction(ConsumerRecord<String, String> record) {
        String message = record.value();

        // Пример JSON: {"accountId":"...","amount":123.45}
        String[] parts = message.replaceAll("[{}\"]", "").split(",");
        UUID accountId = null;
        BigDecimal amount = null;

        for (String part : parts) {
            String[] kv = part.trim().split(":");
            if (kv[0].equals("accountId")) {
                accountId = UUID.fromString(kv[1]);
            } else if (kv[0].equals("amount")) {
                amount = new BigDecimal(kv[1]);
            }
        }

        if (accountId == null || amount == null) return;

        Account account = accountRepository.findByAccountId(accountId).orElse(null);
        if (account == null || account.getStatus() != AccountStatus.OPEN) return;

        Client client = account.getClient();
        ClientStatus clientStatus = client.getStatus();

        if (clientStatus == null || clientStatus == ClientStatus.BLOCKED) {
            String statusStr = clientStatusService.getClientStatus(client.getClientId());

            try {
                clientStatus = ClientStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                clientStatus = ClientStatus.BLOCKED;
            }

            client.setStatus(clientStatus);
        }

        if (clientStatus == ClientStatus.BLOCKED) {
            System.out.println("Client is BLOCKED, skipping transaction for accountId=" + accountId);
            return;
        }

        // Создание транзакции
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Обновление баланса
        BigDecimal oldBalance = account.getBalance();
        BigDecimal newBalance = oldBalance.add(amount);
        account.setBalance(newBalance);

        System.out.println("Before save: accountId=" + account.getAccountId() +
                ", oldBalance=" + oldBalance +
                ", newBalance=" + newBalance);

        accountRepository.save(account);

        // Отправка в Kafka
        String responseMessage = String.format(
                "{\"clientId\":\"%s\",\"accountId\":\"%s\",\"transactionId\":\"%s\",\"timestamp\":\"%s\",\"amount\":%s,\"balance\":%s}",
                client.getClientId(),
                account.getAccountId(),
                transaction.getTransactionId(),
                transaction.getTimestamp(),
                amount.toPlainString(),
                newBalance.toPlainString()
        );

        kafkaTemplate.send("t1_demo_transaction_accept", responseMessage);
    }

    @KafkaListener(topics = "t1_demo_transaction_result", groupId = "transaction-group")
    @Transactional
    public void handleTransactionResult(ConsumerRecord<String, String> record) {
        String message = record.value();

        // Пример JSON: {"transactionId":"...","status":"ACCEPTED"}
        String[] parts = message.replaceAll("[{}\"]", "").split(",");
        UUID transactionId = null;
        String status = null;

        for (String part : parts) {
            String[] kv = part.trim().split(":");
            if (kv[0].equals("transactionId")) {
                transactionId = UUID.fromString(kv[1]);
            } else if (kv[0].equals("status")) {
                status = kv[1];
            }
        }

        if (transactionId == null || status == null) return;

        Transaction transaction = transactionRepository.findByTransactionId(transactionId).orElse(null);
        if (transaction == null) return;

        Account account = transaction.getAccount();
        BigDecimal amount = transaction.getAmount();

        switch (status) {
            case "ACCEPTED":
                transaction.setStatus(TransactionStatus.ACCEPTED);
                transactionRepository.save(transaction);
                break;

            case "REJECTED":
                transaction.setStatus(TransactionStatus.REJECTED);
                account.setBalance(account.getBalance().subtract(amount));
                transactionRepository.save(transaction);
                accountRepository.save(account);
                break;

            case "BLOCKED":
                transaction.setStatus(TransactionStatus.BLOCKED);
                account.setStatus(AccountStatus.BLOCKED);
                account.setFrozenAmount(account.getFrozenAmount().add(amount));
                transactionRepository.save(transaction);
                accountRepository.save(account);
                break;

            default:
                System.err.println("Unknown transaction status: " + status);
        }
    }
}
