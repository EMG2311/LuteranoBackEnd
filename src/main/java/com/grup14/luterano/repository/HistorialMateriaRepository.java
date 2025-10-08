package com.grup14.luterano.repository;

import com.grup14.luterano.entities.HistorialMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialMateriaRepository extends JpaRepository<HistorialMateria,Long> {
    Optional<HistorialMateria> findByHistorialCurso_IdAndMateriaCurso_Id(Long historialCursoId, Long materiaCursoId);
    boolean existsByHistorialCurso_Alumno_IdAndMateriaCurso_Materia_Id(Long alumnoId, Long materiaId);
}
