package com.grup14.luterano.repository;

import com.grup14.luterano.entities.MesaExamenAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MesaExamenAlumnoRepository extends JpaRepository<MesaExamenAlumno, Long> {
    boolean existsByMesaExamen_IdAndAlumno_Id(Long mesaId, Long alumnoId);

    List<MesaExamenAlumno> findByMesaExamen_Id(Long mesaId);

    @Transactional
    @Modifying
    void deleteByMesaExamen_IdAndAlumno_Id(Long mesaId, Long alumnoId);

    // Para reporte anual: finales del alumno dentro de un rango de fechas
    List<MesaExamenAlumno> findByAlumno_IdAndMesaExamen_FechaBetween(Long alumnoId, java.time.LocalDate desde, java.time.LocalDate hasta);
    
    // Para reporte de curso: finales de múltiples alumnos dentro de un rango de fechas
    List<MesaExamenAlumno> findByAlumno_IdInAndMesaExamen_FechaBetween(List<Long> alumnoIds, java.time.LocalDate desde, java.time.LocalDate hasta);
    
    // Para obtener mesas de un alumno para una materia específica (para verificar previas aprobadas)
    List<MesaExamenAlumno> findByAlumno_IdAndMesaExamen_MateriaCurso_Materia_Id(Long alumnoId, Long materiaId);
}
