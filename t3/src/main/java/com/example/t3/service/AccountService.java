package com.example.t3.service;

import com.example.t3.annotation.Cached;
import com.example.t3.annotation.LogDataSourceError;
import com.example.t3.annotation.Metric;
import com.example.t3.model.Account;
import com.example.t3.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    @Metric
    public List<Account> getAllAccounts() {
        try {
            Thread.sleep(600); // искусственная задержка
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return accountRepository.findAll();
    }
    @LogDataSourceError
    @Cached
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));
    }
    @LogDataSourceError
    public List<Account> getAccountsByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId);
    }
    @LogDataSourceError
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account updatedAccount) {
        return accountRepository.findById(id)
                .map(existingAccount -> {
                    existingAccount.setAccountType(updatedAccount.getAccountType());
                    existingAccount.setBalance(updatedAccount.getBalance());
                    return accountRepository.save(existingAccount);
                })
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
