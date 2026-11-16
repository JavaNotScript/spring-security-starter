package com.gicovi.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class CustomSecurityConfig {

    @Bean("customSecurityFilterChain")
    public SecurityFilterChain customSecurityFilterChain(
            HttpSecurity http) throws Exception {
        System.out.println("user defined security config");

        return http
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()
                )
                .build();
    }
}
