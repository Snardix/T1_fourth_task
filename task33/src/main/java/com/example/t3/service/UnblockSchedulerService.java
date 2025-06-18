package com.example.t3.service;

import com.example.t3.model.Account;
import com.example.t3.model.Client;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnblockSchedulerService {

    private final ClientStatusService clientStatusService;
    private final AccountService accountService;

    private final int CLIENT_UNBLOCK_LIMIT = 10;
    private final int ACCOUNT_UNBLOCK_LIMIT = 10;

    public UnblockSchedulerService(ClientStatusService clientStatusService,
                                   AccountService accountService) {
        this.clientStatusService = clientStatusService;
        this.accountService = accountService;
    }

    @Scheduled(fixedDelay = 60000) // раз в минуту
    public void unblockClients() {
        List<Client> blockedClients = clientStatusService.getBlockedClients(CLIENT_UNBLOCK_LIMIT);
        for (Client client : blockedClients) {
            boolean approved = clientStatusService.requestUnblockClient(client.getClientId());
            if (approved) {
                clientStatusService.unblockClient(client.getClientId());
            }
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void unblockAccounts() {
        List<Account> arrestedAccounts = accountService.getArrestedAccounts(ACCOUNT_UNBLOCK_LIMIT);
        for (Account account : arrestedAccounts) {
            boolean approved = accountService.requestUnblockAccount(account.getId());
            if (approved) {
                accountService.unblockAccount(account.getId());
            }
        }
    }
}