package com.example.t3.service;

import com.example.t3.model.Client;
import com.example.t3.model.ClientStatus;
import com.example.t3.repository.ClientRepository;
import com.example.t3.service.UnblockServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class ClientStatusService {

    private final RestTemplate restTemplate;
    private final ClientRepository clientRepository;

    @Value("${client-status.service-url}")
    private String serviceUrl;

    private final UnblockServiceClient unblockServiceClient;


    public ClientStatusService(RestTemplate restTemplate, ClientRepository clientRepository, UnblockServiceClient unblockServiceClient) {
        this.restTemplate = restTemplate;
        this.clientRepository = clientRepository;
        this.unblockServiceClient = unblockServiceClient;
    }

    public String getClientStatus(UUID clientId) {
        String url = serviceUrl + "?clientId=" + clientId;

        try {
            Client client = restTemplate.getForObject(url, Client.class);
            return client != null && client.getStatus() != null
                    ? client.getStatus().name()
                    : "UNKNOWN";
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при запросе статуса клиента: " + e.getMessage());
        }
    }

    public boolean requestUnblockClient(UUID clientId) {
        try {
            return unblockServiceClient.requestClientUnblock(clientId);
        } catch (Exception e) {
            System.err.println("Ошибка при запросе разблокировки клиента: " + e.getMessage());
            return false;
        }
    }

    public List<Client> getBlockedClients(int limit) {
        return clientRepository.findByStatus(ClientStatus.BLOCKED, PageRequest.of(0, limit));
    }

    public void unblockClient(UUID clientId) {
        clientRepository.findByClientId(clientId).ifPresent(client -> {
            client.setStatus(ClientStatus.ACTIVE);
            clientRepository.save(client);
        });
    }

    public long countBlockedClients() {
        return clientRepository.countByStatus(ClientStatus.BLOCKED);
    }
}
