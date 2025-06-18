package com.example.t3.repository;

import com.example.t3.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientId(UUID clientId);
}