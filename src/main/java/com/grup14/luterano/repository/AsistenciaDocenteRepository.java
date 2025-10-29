package com.grup14.luterano.repository;

import com.grup14.luterano.entities.AsistenciaDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaDocenteRepository extends JpaRepository<AsistenciaDocente, Long> {

    Optional<AsistenciaDocente> findByDocente_IdAndFecha(Long docenteId, LocalDate fecha);
    List<AsistenciaDocente> findByFecha(LocalDate fecha);


    // Calcula el total de días en que debio asistir el docente en un período.

    @Query("SELECT COUNT(ad.id) FROM AsistenciaDocente ad WHERE ad.docente.id = :docenteId AND ad.fecha BETWEEN :fechaDesde AND :fechaHasta")

    Long countTotalDiasRegistrados(Long docenteId, LocalDate fechaDesde, LocalDate fechaHasta);

    // Calcula el total de inasistencias del docente en un período. Se condidera el AUSENTE,TARDE Y RETIRO porque afectan el desempeño.

    @Query("""
        SELECT COUNT(ad.id) FROM AsistenciaDocente ad WHERE ad.docente.id = :docenteId AND ad.fecha BETWEEN :fechaDesde AND :fechaHasta
          AND ad.estado IN ('AUSENTE', 'TARDE', 'RETIRO')
    """)
    Long countInasistencias(Long docenteId, LocalDate fechaDesde, LocalDate fechaHasta);

 }



