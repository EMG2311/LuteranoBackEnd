package com.grup14.luterano;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
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

            System.out.println("✅ Roles inicializados correctamente");
        };
    }
}