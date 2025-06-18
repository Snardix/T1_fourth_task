package com.example.t3.service;

import com.example.t3.model.Transaction;
import com.example.t3.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        return transactionRepository.findById(id)
                .map(existingTransaction -> {
                    existingTransaction.setAmount(updatedTransaction.getAmount());
                    existingTransaction.setTimestamp(updatedTransaction.getTimestamp());
                    return transactionRepository.save(existingTransaction);
                })
                .orElseThrow(() -> new RuntimeException("Transaction not found with id " + id));
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}