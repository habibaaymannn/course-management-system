package com.example.publicapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example")
public class PublicApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicApiApplication.class, args);
	}

}
