package com.example.Auth.UserService.config;

import com.example.Auth.UserService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AdminInitialize implements CommandLineRunner {
    private final AuthService authService;

    @Override
    public void run(String... args) throws Exception {
        authService.createADminIfNotThere();
    }

}
