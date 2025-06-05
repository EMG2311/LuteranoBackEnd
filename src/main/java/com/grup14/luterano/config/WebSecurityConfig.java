package com.grup14.luterano.config;

import com.grup14.luterano.auth.infrastructure.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private AuthenticationProvider AuthenticationProvider;
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;
    @Bean
    public JwtAuthFilter jwtAuthFilter(){return new JwtAuthFilter(handlerExceptionResolver);}
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/auth/**"
                                ,"/swagger-ui/**"
                                ,"/swagger-ui.html"
                                ,"/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(AuthenticationProvider)
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

////        return http
////                .csrf(confi -> confi.disable())
////                .authorizeHttpRequests(auth ->
////                {
////                    auth.anyRequest().permitAll();
////
////                })
////                .sessionManagement(session->{
////                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
////                })
////                .build();


    }


}