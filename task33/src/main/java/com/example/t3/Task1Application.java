package com.example.t3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableScheduling
public class Task1Application {

	public static void main(String[] args) {
		SpringApplication.run(Task1Application.class, args);
	}

}
