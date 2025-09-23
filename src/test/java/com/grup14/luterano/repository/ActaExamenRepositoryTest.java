package com.grup14.luterano.repository;

import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.EstadoMesaExamen;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class ActaExamenRepositoryTest {

    @Autowired
    private ActaExamenRepository actaExamenRepository;

    @Autowired
    private MesaExamenRepository mesaExamenRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Test
    void guardarYBuscarActaExamen() {
        // 🟦 Aula
        Aula aula = new Aula();
        aula.setNombre("Aula 303");
        aula.setUbicacion("Tercer piso");
        aula.setCapacidad(25);
        aulaRepository.save(aula);

        // 🟦 Módulo
        Modulo modulo = new Modulo();
        modulo.setHoraDesde(LocalTime.of(9, 0));
        modulo.setHoraHasta(LocalTime.of(11, 0));
        moduloRepository.save(modulo);

        // 🟦 MesaExamen
        MesaExamen mesa = new MesaExamen();
        mesa.setFecha(LocalDate.of(2025, 8, 10));
        mesa.setEstado(EstadoMesaExamen.CREADA);
        mesa.setAula(aula);
        mesa.setModulo(modulo);
        mesaExamenRepository.save(mesa);

        // 🟦 ActaExamen vinculada a la mesa
        ActaExamen acta = new ActaExamen();
        acta.setFecha(LocalDate.of(2025, 8, 11));
        acta.setDetalle("Acta Final de Matemática");
        acta.setMesaExamen(mesa);

        actaExamenRepository.save(acta);

        // 🟦 Verificación
        assertThat(actaExamenRepository.findById(acta.getId()))
                .isPresent()
                .get()
                .extracting(ActaExamen::getDetalle)
                .isEqualTo("Acta Final de Matemática");
    }
}

