package com.example.t3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class UnblockServiceClient {

    private final RestTemplate restTemplate;

    @Value("${unblock-service.url}")
    private String service3Url;

    public UnblockServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean requestClientUnblock(UUID clientId) {
        ResponseEntity<Boolean> response = restTemplate.postForEntity(
                service3Url + "/client/" + clientId,
                null,
                Boolean.class
        );
        return Boolean.TRUE.equals(response.getBody());
    }

    public boolean requestAccountUnblock(Long accountId) {
        ResponseEntity<Boolean> response = restTemplate.postForEntity(
                service3Url + "/account/" + accountId,
                null,
                Boolean.class
        );
        return Boolean.TRUE.equals(response.getBody());
    }
}
