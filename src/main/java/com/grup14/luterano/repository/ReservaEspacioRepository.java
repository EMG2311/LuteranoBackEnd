package com.grup14.luterano.repository;

import com.grup14.luterano.entities.ReservaEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaEspacioRepository extends JpaRepository<ReservaEspacio, Long>, JpaSpecificationExecutor<ReservaEspacio> {

    // Metodo CRÍTICO: Valida si ya existe una reserva ACTIVA (PENDIENTE o APROBADA) para el mismo tiempo/espacio
    @Query("SELECT COUNT(r) > 0 FROM ReservaEspacio r " +
            "WHERE r.espacioAulico.id = :espacioId " +
            "AND r.fecha = :fecha " +
            "AND r.modulo.id = :moduloId " +
            "AND r.estado IN ('PENDIENTE', 'APROBADA')")
    boolean existsActiveReservation(
            @Param("espacioId") Long espacioId,
            @Param("fecha") LocalDate fecha,
            @Param("moduloId") Long moduloId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM ReservaEspacio r " +
            "WHERE r.espacioAulico.id = :espacioId AND r.fecha = :fecha AND r.modulo.id = :moduloId " +
            "AND r.estado IN ('PENDIENTE', 'APROBADA') AND r.id != :currentReservaId")
    boolean existsActiveReservationExcluding(
            @Param("espacioId") Long espacioId,
            @Param("fecha") LocalDate fecha,
            @Param("moduloId") Long moduloId,
            @Param("currentReservaId") Long currentReservaId);


    List<ReservaEspacio> findByUsuarioId(Long usuarioId);

    // Obtener reservas activas para un espacio y fecha específica
    @Query("SELECT r FROM ReservaEspacio r " +
            "WHERE r.espacioAulico.id = :espacioId " +
            "AND r.fecha = :fecha " +
            "AND r.estado IN ('PENDIENTE', 'APROBADA')")
    List<ReservaEspacio> findActiveReservasByEspacioAndFecha(
            @Param("espacioId") Long espacioId,
            @Param("fecha") LocalDate fecha);

}
