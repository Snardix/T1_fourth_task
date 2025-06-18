package com.example.t3.config;

import com.example.t3.jwt.JwtTokenProvider;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public RestTemplateConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors((request, body, execution) -> {
                    String jwt = jwtTokenProvider.generateToken("service-1");
                    request.getHeaders().add("Authorization", "Bearer " + jwt);
                    return execution.execute(request, body);
                })
                .build();
    }
}