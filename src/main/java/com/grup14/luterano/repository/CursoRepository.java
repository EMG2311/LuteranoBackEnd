package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso,Long> {
    Optional<Curso> findById(Long id);
}
