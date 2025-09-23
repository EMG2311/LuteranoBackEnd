package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.entities.enums.Nivel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MateriaRepositoryTest {

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    void testGuardarMateria() {
        Materia materia = Materia.builder()
                .nombre("Matemática I")
                .descripcion("Introducción a los números y operaciones")
                .nivel(Nivel.BASICO)
                .build();

        materiaRepository.save(materia);

        Materia encontrada = materiaRepository.findById(materia.getId()).orElseThrow();

        assertThat(encontrada.getNombre()).isEqualTo("Matemática I");
        assertThat(encontrada.getNivel()).isEqualTo(Nivel.BASICO);
    }
}
