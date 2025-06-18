package com.example.t3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "message")
    private String message;

    @Column(name = "method_signature")
    private String methodSignature;

    @Column(name = "logged_at")
    private LocalDateTime loggedAt;

    public DataSourceErrorLog() {
        this.loggedAt = LocalDateTime.now();
    }

    public DataSourceErrorLog(String stackTrace, String message, String methodSignature) {
        this.stackTrace = stackTrace;
        this.message = message;
        this.methodSignature = methodSignature;
        this.loggedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}