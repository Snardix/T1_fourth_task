package com.example.t3.repository;

import com.example.t3.model.TimeLimitExceedLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeLimitExceedLogRepository extends JpaRepository<TimeLimitExceedLog, Long> {
}