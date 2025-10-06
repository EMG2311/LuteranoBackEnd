package com.grup14.luterano;

import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.entities.Modulo;
import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.repository.ModuloRepository;
import com.grup14.luterano.repository.RoleRepository;
import com.grup14.luterano.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Bean
    CommandLineRunner initModulos(ModuloRepository moduloRepository) {
        record Def(int orden, int dh, int dm, int hh, int hm) {}

        return args -> {
            // upsert por orden (idempotente)
            java.util.function.Consumer<Def> upsert = d -> {
                var nuevo = Modulo.builder()
                        .orden(d.orden())
                        .horaDesde(LocalTime.of(d.dh(), d.dm()))
                        .horaHasta(LocalTime.of(d.hh(), d.hm()))
                        .build();

                moduloRepository.findByOrden(d.orden()).ifPresentOrElse(ex -> {
                    boolean changed = !ex.getHoraDesde().equals(nuevo.getHoraDesde())
                            || !ex.getHoraHasta().equals(nuevo.getHoraHasta());
                    if (changed) {
                        ex.setHoraDesde(nuevo.getHoraDesde());
                        ex.setHoraHasta(nuevo.getHoraHasta());
                        moduloRepository.save(ex);
                    }
                }, () -> moduloRepository.save(nuevo));
            };

            // =============================
            //  GRILLA (medio-módulos)
            //  Jornada 07:15–13:15
            //  Recreos de 10' como gaps:
            //    09:15–09:25, 10:25–10:35, 11:35–11:45
            // =============================
            var defs = List.of(
                    new Def(1,  7,15, 7,45),
                    new Def(2,  7,45, 8,15),
                    new Def(3,  8,15, 8,45),
                    new Def(4,  8,45, 9,15),

                    // gap (recreo) 09:15–09:25

                    new Def(5,  9,25, 9,55),
                    new Def(6,  9,55,10,25),

                    // gap (recreo) 10:25–10:35

                    new Def(7, 10,35,11,05),
                    new Def(8, 11,05,11,35),

                    // gap (recreo) 11:35–11:45

                    new Def(9, 11,45,12,15),
                    new Def(10,12,15,12,45),
                    new Def(11,12,45,13,15)

                    // Si querés uno vespertino extra (medio):
                    // new Def(12, 15,00,15,30)
            );
            defs.forEach(upsert);

            System.out.println("✅ Módulos (medio) inicializados/actualizados");
        };
    }
}