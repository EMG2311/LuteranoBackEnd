package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.entities.MateriaCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MateriaCursoRepository extends JpaRepository<MateriaCurso,Long> {
    Optional<MateriaCurso> findByMateriaId(Long idMateria);
    boolean existsByMateriaIdAndCursoId(Long materiaId, Long cursoId);
    Optional<MateriaCurso> findByMateriaIdAndCursoId(Long materiaId, Long cursoId);
    Optional<MateriaCurso> findByCursoId(Long cursoId);
}
