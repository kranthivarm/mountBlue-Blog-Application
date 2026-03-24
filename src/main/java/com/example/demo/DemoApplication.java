package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
public class DemoApplication {
	public static void main(String[] args) {
//		System.out.println("password\n"+new BCryptPasswordEncoder(12).encode("admin@123"));
		SpringApplication.run(DemoApplication.class, args);
	}
}
