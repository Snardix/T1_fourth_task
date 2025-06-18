package com.example.t3.aspect;

import com.example.t3.kafka.KafkaProducerService;
import com.example.t3.model.TimeLimitExceedLog;
import com.example.t3.repository.TimeLimitExceedLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class MetricAspect {

    private final TimeLimitExceedLogRepository logRepository;
    private final KafkaProducerService kafkaProducerService;

    @Value("${metric.time-limit-millis}")
    private long timeLimitMillis;

    public MetricAspect(TimeLimitExceedLogRepository logRepository,
                        KafkaProducerService kafkaProducerService) {
        this.logRepository = logRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Around("@annotation(com.example.t3.annotation.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        if (executionTime > timeLimitMillis) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String method = methodSignature.toLongString();

            String payload = String.format(
                    "Method %s took %d ms (limit: %d ms)",
                    method, executionTime, timeLimitMillis
            );

            try {
                kafkaProducerService.sendMessage("METRICS", payload);
            } catch (Exception e) {
                TimeLimitExceedLog log = new TimeLimitExceedLog();
                log.setMethodSignature(method);
                log.setExecutionTimeMillis(executionTime);
                log.setExceededAt(LocalDateTime.now());
                logRepository.save(log);
            }
        }

        return result;
    }
}