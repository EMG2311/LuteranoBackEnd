package com.grup14.luterano.repository;

import com.grup14.luterano.entities.AsistenciaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaAlumnoRepository extends JpaRepository<AsistenciaAlumno, Long> {

    Optional<AsistenciaAlumno> findByAlumno_IdAndFecha(Long alumnoId, LocalDate fecha);

    List<AsistenciaAlumno> findByAlumno_CursoActual_IdAndFecha(Long cursoId, LocalDate fecha);
}
