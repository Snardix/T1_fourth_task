package com.example.t3;

import com.example.t3.config.TransactionLimitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableConfigurationProperties(TransactionLimitProperties.class)
public class Task1Application {

	public static void main(String[] args) {
		SpringApplication.run(Task1Application.class, args);
	}

}
