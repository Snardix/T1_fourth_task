package com.example.t3.repository;

import com.example.t3.model.Client;
import com.example.t3.model.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByStatus(ClientStatus status);

    List<Client> findByStatus(ClientStatus status, org.springframework.data.domain.Pageable pageable);

    Optional<Client> findByClientId(UUID clientId);

    long countByStatus(ClientStatus status);
}