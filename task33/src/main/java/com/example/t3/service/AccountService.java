package com.example.t3.service;

import com.example.t3.annotation.Cached;
import com.example.t3.annotation.LogDataSourceError;
import com.example.t3.annotation.Metric;
import com.example.t3.model.Account;
import com.example.t3.model.AccountStatus;
import com.example.t3.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UnblockServiceClient unblockServiceClient;

    public AccountService(AccountRepository accountRepository,
                          UnblockServiceClient unblockServiceClient) {
        this.accountRepository = accountRepository;
        this.unblockServiceClient = unblockServiceClient;
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


    public List<Account> getBlockedAccounts(int limit) {
        return accountRepository.findByStatus(AccountStatus.BLOCKED)
                .stream()
                .limit(limit)
                .toList();
    }

    public List<Account> getArrestedAccounts(int limit) {
        return accountRepository.findByStatus(AccountStatus.BLOCKED)
                .stream()
                .limit(limit)
                .toList();
    }

    public long countArrestedAccounts() {
        return accountRepository.findByStatus(AccountStatus.BLOCKED).size();
    }

    public boolean requestUnblockAccount(Long accountId) {
        try {
            return unblockServiceClient.requestAccountUnblock(accountId);
        } catch (Exception e) {
            System.err.println("Ошибка при запросе разблокировки счёта: " + e.getMessage());
            return false;
        }
    }


    public void unblockAccount(Long accountId) {
        accountRepository.findById(accountId).ifPresent(account -> {
            account.setStatus(AccountStatus.OPEN);
            accountRepository.save(account);
        });
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
