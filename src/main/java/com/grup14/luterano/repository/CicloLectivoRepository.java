package com.grup14.luterano.repository;

import com.grup14.luterano.entities.CicloLectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CicloLectivoRepository extends JpaRepository<CicloLectivo, Long> {
    Optional<CicloLectivo> findByFechaDesdeBeforeAndFechaHastaAfter(LocalDate fecha1, LocalDate fecha2);

    Optional<CicloLectivo> findTopByOrderByFechaHastaDesc();

    boolean existsByNombre(String nombre);

    Optional<CicloLectivo> findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqual(LocalDate desde, LocalDate hasta);

    // Para reporte de desempeño docente: buscar ciclo por año
    @Query("SELECT cl FROM CicloLectivo cl WHERE YEAR(cl.fechaDesde) = :anio")
    Optional<CicloLectivo> findByAnio(@Param("anio") Integer anio);


}
