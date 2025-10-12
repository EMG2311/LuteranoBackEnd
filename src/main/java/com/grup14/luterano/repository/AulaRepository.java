package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Aula;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AulaRepository extends JpaRepository<Aula,Long> {

    Optional<Aula> findById(Long id);
    Optional<Aula> findByNombre(String nombre);
    List<Aula> findByCursoIsNull();


}
