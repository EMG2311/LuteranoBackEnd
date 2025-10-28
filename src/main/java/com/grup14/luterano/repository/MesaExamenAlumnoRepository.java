package com.grup14.luterano.repository;

import com.grup14.luterano.entities.MesaExamenAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaExamenAlumnoRepository extends JpaRepository<MesaExamenAlumno,Long> {
    boolean existsByMesaExamen_IdAndAlumno_Id(Long mesaId, Long alumnoId);
    List<MesaExamenAlumno> findByMesaExamen_Id(Long mesaId);
    void deleteByMesaExamen_IdAndAlumno_Id(Long mesaId, Long alumnoId);

    // Para reporte anual: finales del alumno dentro de un rango de fechas
    List<MesaExamenAlumno> findByAlumno_IdAndMesaExamen_FechaBetween(Long alumnoId, java.time.LocalDate desde, java.time.LocalDate hasta);
}
