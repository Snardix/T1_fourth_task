package com.example.t3.service;

import com.example.t3.model.Client;
import com.example.t3.model.ClientStatus;
import com.example.t3.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final Random random = new Random();

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client getOrCreateClient(UUID clientId) {
        return clientRepository.findByClientId(clientId)
                .orElseGet(() -> {
                    ClientStatus status = random.nextInt(10) == 0 ? ClientStatus.BLOCKED : ClientStatus.ACTIVE;
                    Client client = new Client(null, null, null, clientId, status);
                    return clientRepository.save(client);
                });
    }

    public Optional<Client> findByClientId(UUID clientId) {
        return clientRepository.findByClientId(clientId);
    }
}