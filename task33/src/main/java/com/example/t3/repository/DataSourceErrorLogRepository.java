package com.example.t3.repository;

import com.example.t3.model.DataSourceErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLog, Long> {
}