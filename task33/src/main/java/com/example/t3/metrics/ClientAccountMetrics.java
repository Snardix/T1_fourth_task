package com.example.t3.metrics;

import com.example.t3.service.AccountService;
import com.example.t3.service.ClientStatusService;
import io.micrometer.core.instrument.Metrics;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClientAccountMetrics {

    private final ClientStatusService clientStatusService;
    private final AccountService accountService;

    public ClientAccountMetrics(ClientStatusService clientStatusService,
                                AccountService accountService) {
        this.clientStatusService = clientStatusService;
        this.accountService = accountService;
    }

    @Scheduled(fixedDelay = 60000)
    public void updateMetrics() {
        long blockedClients = clientStatusService.countBlockedClients();
        long arrestedAccounts = accountService.countArrestedAccounts();

        Metrics.gauge("clients.blocked.count", blockedClients);
        Metrics.gauge("accounts.arrested.count", arrestedAccounts);
    }
}