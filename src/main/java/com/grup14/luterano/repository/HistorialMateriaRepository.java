package com.grup14.luterano.repository;

import com.grup14.luterano.entities.HistorialMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialMateriaRepository extends JpaRepository<HistorialMateria,Long> {
}
