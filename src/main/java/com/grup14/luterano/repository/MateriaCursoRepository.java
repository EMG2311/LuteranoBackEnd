package com.grup14.luterano.repository;
import com.grup14.luterano.entities.MateriaCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaCursoRepository extends JpaRepository<MateriaCurso,Long> {
    List<MateriaCurso>findByMateriaId(Long idMateria);
    boolean existsByMateriaIdAndCursoId(Long materiaId, Long cursoId);
    Optional<MateriaCurso> findByMateriaIdAndCursoId(Long materiaId, Long cursoId);
    List<MateriaCurso> findByCursoId(Long cursoId);
}
