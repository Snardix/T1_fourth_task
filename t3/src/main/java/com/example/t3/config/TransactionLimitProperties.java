package com.example.t3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "transaction.limiter")
public class TransactionLimitProperties {

    private int count;
    private int seconds;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public Duration getWindow() {
        return Duration.ofSeconds(seconds);
    }
}
