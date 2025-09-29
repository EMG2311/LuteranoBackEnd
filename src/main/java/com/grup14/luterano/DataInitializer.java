package com.grup14.luterano;

import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.repository.RoleRepository;
import com.grup14.luterano.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(CicloLectivoRepository cicloLectivoRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
            userRepository.findByEmail("admin@gmail.com").orElseGet(()->
                 userRepository.save(User.builder()
                                 .name("Admin")
                                 .lastName("Muy Admin")
                                .email("admin@gmail.com")
                                .password(passwordEncoder.encode("1234"))
                    .userStatus(UserStatus.CREADO)
                            .rol(roleRepository.findByName("ROLE_ADMIN").get())
                            .build())
            );


            System.out.println("✅ Roles inicializados correctamente");

            //Se genera ciclo lectivo 2025 para pruebas
            CicloLectivo cicloLectivo = cicloLectivoRepository.findById(1L)
                    .orElse(null);

            if (cicloLectivo == null) {
                // Creamos manualmente el ciclo lectivo 2025
                CicloLectivo nuevoCiclo = CicloLectivo.builder()
                        // No hace falta poner id, JPA lo genera automáticamente
                        .nombre("Ciclo Lectivo 2025")
                        .fechaDesde(LocalDate.of(2025, 1, 1))  // 1 de enero de 2025
                        .fechaHasta(LocalDate.of(2025, 12, 31)) // 31 de diciembre de 2025
                        .build();

                cicloLectivoRepository.save(nuevoCiclo);
            }

        };
    }
}