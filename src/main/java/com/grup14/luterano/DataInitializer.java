package com.grup14.luterano;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.repository.RoleRepository;
import com.grup14.luterano.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            List<String> roles = List.of(
                    "ROLE_ADMIN",
                    "ROLE_DIRECTOR",
                    "ROLE_DOCENTE",
                    "ROLE_ALUMNO",
                    "ROLE_VISITA",
                    "ROLE_PRECEPTOR"
            );

            for (String roleName : roles) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            com.grup14.luterano.entities.Role role = new Role();
                            role.setName(roleName);
                            return roleRepository.save(role);
                        });
            }
            userRepository.findByEmail("admnin@gmail.com").orElseGet(()->
                 userRepository.save(User.builder()
                                .email("admnin@gmail.com")
                                .password(passwordEncoder.encode("44575808"))
                    .userStatus(UserStatus.CREADO)
                            .rol(roleRepository.findByName("ROLE_ADMIN").get())
                            .build())
            );


            System.out.println("✅ Roles inicializados correctamente");
        };
    }
}