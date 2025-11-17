package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    boolean existsByNombreAndActivaTrue(String nombre);
    boolean existsByNombre(String nombre);
    Materia findByNombre(String nombre);
    Materia findByNombreAndActivaFalse(String nombre);
    java.util.List<Materia> findAllByActivaTrue();
}
