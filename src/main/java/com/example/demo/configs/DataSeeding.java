package com.example.demo.configs;

import com.example.demo.entities.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeding {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner AdminSeeding(){
        return args->{
            String adminEmail = "admin@gmail.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                UserEntity admin = new UserEntity();
                admin.setName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Admin seeded: " + adminEmail + " / Admin@123");
            }
        };
    }
}
