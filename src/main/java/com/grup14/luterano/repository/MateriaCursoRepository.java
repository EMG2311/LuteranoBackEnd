package com.grup14.luterano.repository;
import com.grup14.luterano.entities.MateriaCurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MateriaCursoRepository extends JpaRepository<MateriaCurso,Long> {
    Optional<MateriaCurso> findByMateriaId(Long idMateria);
    boolean existsByMateriaIdAndCursoId(Long materiaId, Long cursoId);
    Optional<MateriaCurso> findByMateriaIdAndCursoId(Long materiaId, Long cursoId);
    Optional<MateriaCurso> findByCursoId(Long cursoId);
}
