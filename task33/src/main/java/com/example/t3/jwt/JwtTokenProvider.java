package com.example.t3.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secretKey = "my-super-secret-key-123456";

    public String generateToken(String serviceName) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 3600000); // 1 час

        return Jwts.builder()
                .setSubject(serviceName)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}