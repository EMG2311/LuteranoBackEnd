package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocenteRepositoryTest extends BaseRepositoryTest {

    @Test
    void guardarYBuscarDocente() {
        Role rol = createRole("DOCENTE");
        User user = createUser("Carlos", "Perez", "carlos@test.com", rol);
        Docente docente = createDocente("12345678", "carlos@test.com", user);

        assertThat(docenteRepository.findByEmail("carlos@test.com"))
                .isPresent()
                .get()
                .extracting(Docente::getNombre)
                .isEqualTo("Carlos");

        assertThat(docenteRepository.findByDni("12345678")).isPresent();
    }
}
