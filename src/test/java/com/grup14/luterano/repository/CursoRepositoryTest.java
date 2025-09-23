package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CursoRepositoryTest {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Test
    void testGuardarCursoConAula() {
        Aula aula = Aula.builder()
                .nombre("Aula 101")
                .ubicacion("Primer piso")
                .capacidad(30)
                .build();
        aulaRepository.save(aula);

        Curso curso = Curso.builder()
                .anio(1)
                .division(Division.A)
                .nivel(Nivel.BASICO)
                .aula(aula)
                .build();
        cursoRepository.save(curso);

        Curso encontrado = cursoRepository.findById(curso.getId()).orElseThrow();

        assertThat(encontrado.getAula().getNombre()).isEqualTo("Aula 101");
        assertThat(encontrado.getDivision()).isEqualTo(Division.A);
        assertThat(encontrado.getNivel()).isEqualTo(Nivel.BASICO);
    }
}
