package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.Tutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void guardarYBuscarRol() {
        Role rol = new Role();
        rol.setName("DOCENTE");

        roleRepository.save(rol);

        assertThat(roleRepository.findByName("DOCENTE"))
                .isPresent()
                .get()
                .extracting(Role::getName)
                .isEqualTo("DOCENTE");
    }

    @ActiveProfiles("test")
    @DataJpaTest
    static
    class TutorRepositoryTest {

        @Autowired
        private TutorRepository tutorRepository;

        @Test
        void guardarYBuscarTutorPorDni() {
            Tutor tutor = new Tutor();
            tutor.setNombre("Laura");
            tutor.setApellido("Martinez");
            tutor.setDni("87654321");
            tutor.setEmail("laura@test.com");

            tutorRepository.save(tutor);

            assertThat(tutorRepository.findByDni("87654321"))
                    .isPresent()
                    .get()
                    .extracting(Tutor::getNombre)
                    .isEqualTo("Laura");
        }
    }
}
