package com.example.t3.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CachedAspect {

    private static class CacheEntry {
        Object value;
        Instant expireAt;

        CacheEntry(Object value, Instant expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Value("${cache.ttl-seconds}")
    private long ttlSeconds;

    @Around("@annotation(com.example.t3.annotation.Cached)")
    public Object cacheMethodResult(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String key = generateCacheKey(method, joinPoint.getArgs());

        CacheEntry entry = cache.get(key);
        if (entry != null && Instant.now().isBefore(entry.expireAt)) {
            System.out.println("🔁 Returning cached value for " + key);
            return entry.value;
        }

        Object result = joinPoint.proceed();
        cache.put(key, new CacheEntry(result, Instant.now().plusSeconds(ttlSeconds)));
        System.out.println("✅ Cached value for " + key);

        return result;
    }

    private String generateCacheKey(Method method, Object[] args) {
        return method.toGenericString() + Arrays.toString(args);
    }
}