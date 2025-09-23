package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AlumnoRepositoryTest {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Test
    void testGuardarYBuscarAlumno() {
        // Crear un alumno
        Alumno alumno = Alumno.builder()
                .nombre("Agostina")
                .apellido("Torres")
                .dni("12345678")
                .email("agos@example.com")
                .estado(EstadoAlumno.REGULAR) // usa tu enum
                .build();

        // Guardar en la BD H2
        Alumno alumnoGuardado = alumnoRepository.save(alumno);

        // Buscar por id
        Optional<Alumno> alumnoEncontrado = alumnoRepository.findById(alumnoGuardado.getId());

        // Verificaciones
        assertThat(alumnoEncontrado).isPresent();
        assertThat(alumnoEncontrado.get().getNombre()).isEqualTo("Agostina");
        assertThat(alumnoEncontrado.get().getApellido()).isEqualTo("Torres");
        assertThat(alumnoEncontrado.get().getEmail()).isEqualTo("agos@example.com");
    }
}
