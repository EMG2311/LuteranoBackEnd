package com.grup14.luterano.repository;

import com.grup14.luterano.entities.CicloLectivo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class CicloLectivoRepositoryTest {

    @Autowired
    private CicloLectivoRepository cicloLectivoRepository;

    @Test
    void guardarYBuscarCicloLectivo() {
        // Crear ciclo lectivo
        CicloLectivo ciclo = new CicloLectivo();
        ciclo.setNombre("Ciclo 2025");

        cicloLectivoRepository.save(ciclo);

        // Verificación
        assertThat(cicloLectivoRepository.findById(ciclo.getId()))
                .isPresent()
                .get()
                .extracting(CicloLectivo::getNombre)
                .isEqualTo("Ciclo 2025");
    }
}
