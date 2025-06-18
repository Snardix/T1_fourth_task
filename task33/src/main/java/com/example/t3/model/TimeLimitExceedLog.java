package com.example.t3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_limit_exceed_log")
public class TimeLimitExceedLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String methodSignature;

    private Long executionTimeMillis;

    private LocalDateTime exceededAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public Long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public void setExecutionTimeMillis(Long executionTimeMillis) {
        this.executionTimeMillis = executionTimeMillis;
    }

    public LocalDateTime getExceededAt() {
        return exceededAt;
    }

    public void setExceededAt(LocalDateTime exceededAt) {
        this.exceededAt = exceededAt;
    }
}
