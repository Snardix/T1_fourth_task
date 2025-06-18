package com.example.t3.repository;

import com.example.t3.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.t3.model.AccountStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByClientId(Long clientId);
    Optional<Account> findByAccountId(UUID accountId);
    List<Account> findByStatus(AccountStatus status);
}
