package com.grup14.luterano.repository;

import com.grup14.luterano.entities.HistorialCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface HistorialCursoRepository extends JpaRepository<HistorialCurso,Long> {
    Optional<HistorialCurso> findByAlumno_IdAndCicloLectivo_IdAndFechaHastaIsNull(Long alumnoId, Long cicloLectivoId);
    List<HistorialCurso> findByAlumno_IdAndCicloLectivo_IdAndCurso_Id(Long alumnoId, Long cicloLectivoId, Long cursoId);
    List<HistorialCurso> findByAlumno_IdAndCicloLectivo_Id(Long alumnoId, Long cicloLectivoId);
    List<HistorialCurso> findByAlumno_IdAndCurso_Id(Long alumnoId, Long cursoId);
    List<HistorialCurso> findByAlumno_Id(Long alumnoId);
    Optional<HistorialCurso> findByAlumno_IdAndFechaHastaIsNull(Long alumnoId);
}
