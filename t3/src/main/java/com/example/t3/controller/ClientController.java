package com.example.t3.controller;

import com.example.t3.model.Client;
import com.example.t3.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Map<String, String>> getClientStatus(@PathVariable UUID clientId) {
        Client client = clientService.getOrCreateClient(clientId);
        return ResponseEntity.ok(Map.of("clientId", client.getClientId().toString(), "status", client.getStatus().name()));
    }
}