package com.grup14.luterano.repository;

import com.grup14.luterano.entities.MesaExamenDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MesaExamenDocenteRepository extends JpaRepository<MesaExamenDocente, Long> {

    List<MesaExamenDocente> findByMesaExamen_Id(Long mesaExamenId);

    void deleteByMesaExamen_Id(Long mesaExamenId);

    @Query("SELECT COUNT(med) FROM MesaExamenDocente med WHERE med.mesaExamen.id = :mesaExamenId")
    long countByMesaExamen_Id(@Param("mesaExamenId") Long mesaExamenId);

    @Query("SELECT COUNT(med) FROM MesaExamenDocente med WHERE med.mesaExamen.id = :mesaExamenId AND med.esDocenteMateria = true")
    long countDocentesMateriaByMesaExamen_Id(@Param("mesaExamenId") Long mesaExamenId);

    @Query("""
                SELECT med.docente.id 
                FROM MesaExamenDocente med 
                WHERE med.mesaExamen.fecha = :fecha 
                AND med.docente.id IN :docenteIds
            """)
    List<Long> findDocentesConflictoEnFecha(@Param("fecha") LocalDate fecha, @Param("docenteIds") List<Long> docenteIds);

    @Query("""
                SELECT COUNT(med) > 0 
                FROM MesaExamenDocente med 
                WHERE med.docente.id = :docenteId 
                AND med.mesaExamen.fecha = :fecha 
                AND med.mesaExamen.id <> :mesaExamenId
            """)
    boolean existeDocenteEnOtraMesaEnFecha(@Param("docenteId") Long docenteId,
                                           @Param("fecha") LocalDate fecha,
                                           @Param("mesaExamenId") Long mesaExamenId);

    /**
     * Devuelve los IDs de docentes que ya tienen una mesa
     * en la misma fecha y con horario que se solapa.
     *
     * Se excluye la mesa con id = :mesaId (para usar en edición).
     */
    @Query("""
           select distinct med.docente.id
           from MesaExamenDocente med
           join med.mesaExamen me
           where me.fecha = :fecha
             and med.docente.id in :docenteIds
             and (:mesaId is null or me.id <> :mesaId)
             and me.horaInicio < :horaFin
             and me.horaFin > :horaInicio
           """)
    List<Long> findDocentesConflictoEnFechaYHorario(
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("docenteIds") List<Long> docenteIds,
            @Param("mesaId") Long mesaId
    );

    @Query("""
           select case when count(med) > 0 then true else false end
           from MesaExamenDocente med
           join med.mesaExamen me
           where me.fecha = :fecha
             and med.docente.id = :docenteId
             and me.id <> :mesaId
             and me.horaInicio < :horaFin
             and me.horaFin > :horaInicio
           """)
    boolean existeDocenteEnOtraMesaEnFechaYHorario(
            @Param("docenteId") Long docenteId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("mesaId") Long mesaId
    );
}