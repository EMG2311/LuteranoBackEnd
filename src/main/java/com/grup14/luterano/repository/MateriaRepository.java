package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia,Long> {
    boolean existsByNombre(String nombre);
}
