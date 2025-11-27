package com.grup14.luterano.repository;

import com.grup14.luterano.entities.MesaExamen;
import com.grup14.luterano.entities.enums.TipoMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MesaExamenRepository extends JpaRepository<MesaExamen, Long> {
    boolean existsByAulaId(Long aulaId);
    List<MesaExamen> findByTurno_IdAndTipoMesaAndMateriaCurso_Materia_Id(
            Long turnoId, TipoMesa tipoMesa, Long materiaId
    );

    @Query("""
        SELECT DISTINCT me
        FROM MesaExamenDocente med
        JOIN med.mesaExamen me
        WHERE med.docente.id IN :docenteIds
          AND me.fecha = :fecha
          AND me.id <> :mesaId
          AND me.horaInicio < :horaFin
          AND me.horaFin > :horaInicio
    """)
    List<MesaExamen> findMesasConflictoParaDocentesEnHorario(@Param("fecha") LocalDate fecha,
                                                             @Param("horaInicio") LocalTime horaInicio,
                                                             @Param("horaFin") LocalTime horaFin,
                                                             @Param("docenteIds") List<Long> docenteIds,
                                                             @Param("mesaId") Long mesaId);

    // Mesas que generan conflicto para un docente en particular
    @Query("""
        SELECT DISTINCT me
        FROM MesaExamenDocente med
        JOIN med.mesaExamen me
        WHERE med.docente.id = :docenteId
          AND me.fecha = :fecha
          AND me.id <> :mesaId
          AND me.horaInicio < :horaFin
          AND me.horaFin > :horaInicio
    """)
    List<MesaExamen> findMesasConflictoParaDocenteEnHorario(@Param("docenteId") Long docenteId,
                                                            @Param("fecha") LocalDate fecha,
                                                            @Param("horaInicio") LocalTime horaInicio,
                                                            @Param("horaFin") LocalTime horaFin,
                                                            @Param("mesaId") Long mesaId);

    @Query("""
              select distinct m from MesaExamen m
              join fetch m.materiaCurso mc
              join fetch mc.curso c
              join fetch mc.materia mat
              left join fetch m.aula au
              left join fetch m.alumnos al
              left join fetch al.alumno a
              left join fetch al.turno t
              where mc.id = :materiaCursoId
              order by m.fecha asc, mat.nombre asc
            """)
    List<MesaExamen> findByMateriaCursoIdWithAlumnos(Long materiaCursoId);

    @Query("""
              select distinct m from MesaExamen m
              join fetch m.materiaCurso mc
              join fetch mc.curso c
              join fetch mc.materia mat
              left join fetch m.aula au
              left join fetch m.alumnos al
              left join fetch al.alumno a
              left join fetch al.turno t
              where mc.curso.id = :cursoId
              order by m.fecha asc, mat.nombre asc
            """)
    List<MesaExamen> findByCursoIdWithAlumnos(Long cursoId);

    @Query("""
              select distinct m from MesaExamen m
              join fetch m.materiaCurso mc
              join fetch mc.curso c
              join fetch mc.materia mat
              left join fetch m.aula au
              left join fetch m.alumnos al
              left join fetch al.alumno a
              where m.turno.id = :turnoId
              order by m.fecha asc, mat.nombre asc
            """)
    List<MesaExamen> findByTurnoIdWithAlumnos(Long turnoId);

    @Query("""
                select m
                from MesaExamen m
                where m.turno.id = :turnoId
                  and (m.fecha < :desde or m.fecha > :hasta)
            """)
    List<MesaExamen> findByTurnoOutsideRange(Long turnoId, LocalDate desde, LocalDate hasta);

    @Query("""
            SELECT m 
            FROM MesaExamen m 
            JOIN m.docentes md 
            WHERE md.docente.id = :docenteId 
            AND m.fecha = :fecha 
            AND m.id <> :excludeMesaId
            """)
    List<MesaExamen> findMesasConflictoParaDocente(Long docenteId, LocalDate fecha, Long excludeMesaId);

    @Query("""
            select m from MesaExamen m
            left join fetch m.alumnos al
            left join fetch al.alumno a
            left join fetch m.materiaCurso mc
            left join fetch mc.materia mat
            left join fetch mc.curso c
            left join fetch m.aula au
            left join fetch m.turno t
            where m.id = :id
            """)
    Optional<MesaExamen> findByIdWithAlumnos(Long id);

    @Query("""
        SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END
        FROM MesaExamen m
        WHERE m.fecha = :fecha
          AND m.aula.id = :aulaId
          AND (:mesaId IS NULL OR m.id <> :mesaId)
          AND m.horaInicio < :horaFin
          AND m.horaFin > :horaInicio
    """)
    boolean existsAulaEnUsoEnHorario(@Param("aulaId") Long aulaId,
                                     @Param("fecha") LocalDate fecha,
                                     @Param("horaInicio") LocalTime horaInicio,
                                     @Param("horaFin") LocalTime horaFin,
                                     @Param("mesaId") Long mesaId);

    @Query("""
        SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END
        FROM MesaExamen m
        WHERE m.turno.id = :turnoId
          AND m.materiaCurso.id = :materiaCursoId
          AND m.tipoMesa = :tipoMesa
          AND (:mesaId IS NULL OR m.id <> :mesaId)
    """)
    boolean existsOtraMesaMismoTipoMateriaTurno(@Param("turnoId") Long turnoId,
                                                @Param("materiaCursoId") Long materiaCursoId,
                                                @Param("tipoMesa") TipoMesa tipoMesa,
                                                @Param("mesaId") Long mesaId);
}
