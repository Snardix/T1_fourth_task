package com.example.t3.datagenerator;

import com.example.t3.model.*;
import com.example.t3.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TestDataGenerator implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TestDataGenerator(ClientRepository clientRepository,
                             AccountRepository accountRepository,
                             TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("📦 Генерация тестовых данных...");

        for (int i = 1; i <= 5; i++) {
            // Создание клиента
            Client client = new Client(
                    "LastName" + i,
                    "FirstName" + i,
                    "MiddleName" + i,
                    UUID.randomUUID()
            );
            client = clientRepository.save(client);

            // Создание 2 аккаунтов для клиента
            for (int j = 0; j < 2; j++) {
                Account account = new Account();
                account.setClient(client);
                account.setAccountType(j % 2 == 0 ? AccountType.DEBIT : AccountType.CREDIT);
                BigDecimal balance = BigDecimal.valueOf(Math.random() * 10000.0).setScale(2, BigDecimal.ROUND_HALF_UP);
                account.setBalance(balance);
                account.setAccountId(UUID.randomUUID());
                account.setStatus(AccountStatus.OPEN);
                account.setFrozenAmount(BigDecimal.ZERO);

                account = accountRepository.save(account);

                // Создание транзакций для аккаунта
                for (int k = 0; k < 3; k++) {
                    BigDecimal amount = BigDecimal.valueOf(Math.random() * 1000.0).setScale(2, BigDecimal.ROUND_HALF_UP);
                    Transaction transaction = new Transaction(account, amount, TransactionStatus.REQUESTED);
                    transactionRepository.save(transaction);
                }
            }
        }

        System.out.println("Генерация завершена.");
    }
}
