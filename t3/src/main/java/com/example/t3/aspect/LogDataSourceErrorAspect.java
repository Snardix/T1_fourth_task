package com.example.t3.aspect;

import com.example.t3.kafka.KafkaProducerService;
import com.example.t3.model.DataSourceErrorLog;
import com.example.t3.repository.DataSourceErrorLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Aspect
@Component
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository logRepository;
    private final KafkaProducerService kafkaProducerService;

    public LogDataSourceErrorAspect(DataSourceErrorLogRepository logRepository,
                                    KafkaProducerService kafkaProducerService) {
        this.logRepository = logRepository;
        this.kafkaProducerService = kafkaProducerService;
        System.out.println("✅ LogDataSourceErrorAspect initialized!");
    }

    @Around("@annotation(com.example.t3.annotation.LogDataSourceError)")
    public Object logError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodSignature = signature.toLongString();

            String payload = String.format(
                    "Data source error in %s: %s\n%s",
                    methodSignature, ex.getMessage(), stackTrace
            );

            try {
                kafkaProducerService.sendMessage("DATA_SOURCE", payload);
            } catch (Exception e) {
                DataSourceErrorLog log = new DataSourceErrorLog();
                log.setStackTrace(stackTrace);
                log.setMessage(ex.getMessage());
                log.setMethodSignature(methodSignature);
                log.setLoggedAt(LocalDateTime.now());
                logRepository.save(log);
            }

            throw ex;
        }
    }
}
