package com.grup14.luterano.repository;

import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.Nivel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class CalificacionRepositoryTest {

    @Autowired CalificacionRepository calificacionRepository;
    @Autowired AlumnoRepository alumnoRepository;
    @Autowired TutorRepository tutorRepository;
    @Autowired MateriaRepository materiaRepository;
    @Autowired CicloLectivoRepository cicloLectivoRepository;

    @Test
    void guardarYBuscarCalificacion() {
        // 🟦 Tutor (obligatorio para Alumno)
        Tutor tutor = new Tutor();
        tutor.setNombre("Carlos");
        tutor.setApellido("Gomez");
        tutor.setDni("11223344");
        tutor.setEmail("carlos@test.com");
        tutor = tutorRepository.save(tutor);

        // 🟦 Alumno
        Alumno alumno = new Alumno();
        alumno.setNombre("Pedro");
        alumno.setApellido("Suarez");
        alumno.setDni("44556677");
        alumno.setEmail("pedro@test.com");
        alumno.setTutor(tutor);
        alumno = alumnoRepository.save(alumno);

        // 🟦 Materia (NOT NULL: nombre, descripcion, nivel)
        Materia materia = new Materia();
        materia.setNombre("Lengua");
        materia.setDescripcion("Lengua Castellana");
        materia.setNivel(Nivel.BASICO);
        materia = materiaRepository.save(materia);

        // 🟦 Ciclo Lectivo
        CicloLectivo ciclo = new CicloLectivo();
        ciclo.setNombre("Ciclo 2025");
        ciclo = cicloLectivoRepository.save(ciclo);

        // 🟦 Calificación (usar builder con .PG porque el campo es mayúscula)
        Calificacion calificacion = Calificacion.builder()
                .fecha(LocalDate.of(2025, 6, 20))
                .nota(8.5f)
                .numeroNota(1)
                .PG(9.0f)   // 👈 importante: PG en mayúsculas
                .alumno(alumno)
                .materia(materia)
                .cicloLectivo(ciclo)
                .build();

        calificacion = calificacionRepository.save(calificacion);

        // 🟦 Verificación
        assertThat(calificacionRepository.findById(calificacion.getId()))
                .isPresent()
                .get()
                .extracting(Calificacion::getNota)
                .isEqualTo(8.5f);
    }
}
