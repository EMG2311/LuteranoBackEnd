package com.grup14.luterano.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(confi -> confi.disable())
                .authorizeHttpRequests(auth ->
                {
                    auth.anyRequest().permitAll();

                })
                .sessionManagement(session->{
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .build();
    }
}