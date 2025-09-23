package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.MesaExamen;
import com.grup14.luterano.entities.Modulo;
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
class MesaExamenRepositoryTest {

    @Autowired
    private MesaExamenRepository mesaExamenRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Test
    void guardarYBuscarMesaExamen() {
        // 🟦 Crear Aula
        Aula aula = new Aula();
        aula.setNombre("Aula 101");
        aula.setUbicacion("Primer piso");
        aula.setCapacidad(30); // ✅ ahora no va null
        aulaRepository.save(aula);

        // 🟦 Crear Módulo
        Modulo modulo = new Modulo();
        modulo.setHoraDesde(LocalTime.of(8, 0));
        modulo.setHoraHasta(LocalTime.of(10, 0));
        moduloRepository.save(modulo);

        // 🟦 Crear MesaExamen
        MesaExamen mesa = new MesaExamen();
        mesa.setFecha(LocalDate.of(2025, 6, 15));
        mesa.setEstado(EstadoMesaExamen.CREADA);
        mesa.setAula(aula);
        mesa.setModulo(modulo);

        mesaExamenRepository.save(mesa);

        // 🟦 Verificar
        assertThat(mesaExamenRepository.findById(mesa.getId()))
                .isPresent()
                .get()
                .extracting(MesaExamen::getEstado)
                .isEqualTo(EstadoMesaExamen.CREADA);
    }
};