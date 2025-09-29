package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion,Long> {
    List<Calificacion> findByHistorialMateria_Id(Long historialMateriaId);
}
