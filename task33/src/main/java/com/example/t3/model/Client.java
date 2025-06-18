package com.example.t3.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "client_id", unique = true, nullable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClientStatus status;

    @PrePersist
    public void prePersist() {
        if (this.clientId == null) {
            this.clientId = UUID.randomUUID();
        }
        if (this.status == null) {
            this.status = ClientStatus.ACTIVE;
        }
    }

    public Client() {
    }

    public Client(String lastName, String firstName, String middleName, UUID clientId, ClientStatus status) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.clientId = clientId;
        this.status = status;
    }

    // --- Getters and setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }
}
