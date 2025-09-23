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
class MesaExamenAlumnoRepositoryTest {

    @Autowired
    private MesaExamenAlumnoRepository mesaExamenAlumnoRepository;

    @Autowired
    private MesaExamenRepository mesaExamenRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Test
    void guardarYBuscarMesaExamenAlumno() {
        // 🟦 Aula obligatoria
        Aula aula = new Aula();
        aula.setNombre("Aula 202");
        aula.setUbicacion("Segundo piso");
        aula.setCapacidad(40);
        aulaRepository.save(aula);

        // 🟦 Módulo obligatorio
        Modulo modulo = new Modulo();
        modulo.setHoraDesde(LocalTime.of(14, 0));
        modulo.setHoraHasta(LocalTime.of(16, 0));
        moduloRepository.save(modulo);

        // 🟦 MesaExamen
        MesaExamen mesa = new MesaExamen();
        mesa.setFecha(LocalDate.of(2025, 7, 20));
        mesa.setEstado(EstadoMesaExamen.CREADA);
        mesa.setAula(aula);
        mesa.setModulo(modulo);
        mesaExamenRepository.save(mesa);

        // 🟦 Alumno (mínimo nombre + apellido + dni)
        Alumno alumno = new Alumno();
        alumno.setNombre("Lucia");
        alumno.setApellido("Torres");
        alumno.setDni("87654321");
        alumnoRepository.save(alumno);

        // 🟦 Relación MesaExamenAlumno
        MesaExamenAlumno relacion = new MesaExamenAlumno();
        relacion.setMesaExamen(mesa);
        relacion.setAlumno(alumno);

        mesaExamenAlumnoRepository.save(relacion);

        // 🟦 Verificación
        assertThat(mesaExamenAlumnoRepository.findById(relacion.getId()))
                .isPresent()
                .get()
                .extracting(MesaExamenAlumno::getAlumno)
                .extracting(Alumno::getNombre)
                .isEqualTo("Lucia");
    }
}
