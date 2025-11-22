package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    List<Calificacion> findByHistorialMateria_Id(Long historialMateriaId);


    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
        FROM Calificacion c
        JOIN c.historialMateria hm
        JOIN hm.historialCurso hc
        WHERE hc.alumno.id = :alumnoId
          AND hm.materiaCurso.id = :materiaCursoId
          AND year(hc.cicloLectivo.fechaDesde) = :cicloLectivoAnio
          AND c.nota < 6
    """)
    boolean existeEtapaDesaprobada(@Param("alumnoId") Long alumnoId,
                                   @Param("materiaCursoId") Long materiaCursoId,
                                   @Param("cicloLectivoAnio") int cicloLectivoAnio);

    boolean existsByHistorialMateria_IdAndEtapaAndNumeroNota(Long hmId, int etapa, int numeroNota);

    @Query("""
              select c
              from Calificacion c
                join c.historialMateria hm
                join hm.historialCurso hc
                join hm.materiaCurso mc
              where hc.alumno.id = :alumnoId
                and mc.materia.id = :materiaId
              order by c.etapa asc, c.numeroNota asc, c.fecha asc
            """)
    List<Calificacion> findByAlumnoAndMateria(
            @org.springframework.data.repository.query.Param("alumnoId") Long alumnoId,
            @org.springframework.data.repository.query.Param("materiaId") Long materiaId);

    @Query("""
              select c
              from Calificacion c
                join c.historialMateria hm
                join hm.historialCurso hc
                join hm.materiaCurso mc
              where c.id = :califId
                and hc.alumno.id = :alumnoId
                and mc.materia.id = :materiaId
            """)
    Optional<Calificacion> findOwned(@Param("alumnoId") Long alumnoId,
                                     @Param("materiaId") Long materiaId,
                                     @Param("califId") Long califId);

    @Query("""
              select c
              from Calificacion c
                join c.historialMateria hm
                join hm.historialCurso hc
              where hc.alumno.id = :alumnoId
                and c.fecha between :desde and :hasta
              order by c.fecha asc, c.etapa asc, c.numeroNota asc
            """)
    List<Calificacion> findByAlumnoAndAnio(@Param("alumnoId") Long alumnoId,
                                           @Param("desde") LocalDate desde,
                                           @Param("hasta") LocalDate hasta);

    @Query("""
              select c
              from Calificacion c
                join c.historialMateria hm
                join hm.historialCurso hc
              where hc.alumno.id = :alumnoId
                and c.etapa = :etapa
                and c.fecha between :desde and :hasta
              order by c.fecha asc, c.numeroNota asc
            """)
    List<Calificacion> findByAlumnoAndAnioAndEtapa(@Param("alumnoId") Long alumnoId,
                                                   @Param("etapa") int etapa,
                                                   @Param("desde") LocalDate desde,
                                                   @Param("hasta") LocalDate hasta);

    @Query("""
                select c
                from Calificacion c
                join c.historialMateria hm
                join hm.historialCurso hc
                join hm.materiaCurso mc
                where hc.alumno.id in :alumnoIds
                  and c.fecha between :desde and :hasta
                order by hc.alumno.id, mc.materia.id, c.etapa, c.numeroNota, c.fecha
            """)
    List<Calificacion> findByAlumnosAndAnio(@Param("alumnoIds") List<Long> alumnoIds,
                                            @Param("desde") LocalDate desde,
                                            @Param("hasta") LocalDate hasta);

    Optional<Calificacion> findByHistorialMateria_IdAndEtapaAndNumeroNota(Long hmId, int etapa, int numeroNota);

    @Query("""
                select c
                from Calificacion c
                join c.historialMateria hm
                join hm.historialCurso hc
                join hm.materiaCurso mc
                where hc.alumno.id in :alumnoIds
                  and hc.cicloLectivo.id = :cicloId
                  and mc.curso.id = :cursoId
                  and c.fecha between :desde and :hasta
                order by hc.alumno.id, mc.materia.id, c.etapa, c.numeroNota, c.fecha
            """)
    List<Calificacion> findByAlumnosCursoCicloAndAnio(@Param("alumnoIds") List<Long> alumnoIds,
                                                      @Param("cursoId") Long cursoId,
                                                      @Param("cicloId") Long cicloId,
                                                      @Param("desde") java.time.LocalDate desde,
                                                      @Param("hasta") java.time.LocalDate hasta);

    // Para reporte de exámenes consecutivos desaprobados
    @Query("""
        SELECT c
        FROM Calificacion c
        JOIN c.historialMateria hm
        JOIN hm.historialCurso hc
        JOIN hm.materiaCurso mc
        JOIN hc.alumno a
        JOIN mc.materia m
        JOIN mc.curso cur
        WHERE YEAR(hc.cicloLectivo.fechaDesde) = :cicloLectivoAnio
        AND hc.fechaHasta IS NULL
        AND hm.estado IN ('CURSANDO', 'EXAMEN', 'COLOQUIO', 'PENDIENTE_EXAMEN')
        ORDER BY a.id, m.id, c.etapa, c.numeroNota
    """)
    List<Calificacion> findCalificacionesParaAnalisisConsecutivo(@Param("cicloLectivoAnio") Integer cicloLectivoAnio);

    // Para reporte de exámenes consecutivos filtrado por materia específica
    @Query("""
        SELECT c
        FROM Calificacion c
        JOIN c.historialMateria hm
        JOIN hm.historialCurso hc
        JOIN hm.materiaCurso mc
        JOIN hc.alumno a
        JOIN mc.materia m
        JOIN mc.curso cur
        WHERE YEAR(hc.cicloLectivo.fechaDesde) = :cicloLectivoAnio
        AND hc.fechaHasta IS NULL
        AND hm.estado IN ('CURSANDO', 'EXAMEN', 'COLOQUIO', 'PENDIENTE_EXAMEN')
        AND m.id = :materiaId
        ORDER BY a.id, c.etapa, c.numeroNota
    """)
    List<Calificacion> findCalificacionesParaAnalisisConsecutivoPorMateria(@Param("cicloLectivoAnio") Integer cicloLectivoAnio, 
                                                                           @Param("materiaId") Long materiaId);

    // Para reporte de exámenes consecutivos filtrado por curso específico
    @Query("""
        SELECT c
        FROM Calificacion c
        JOIN c.historialMateria hm
        JOIN hm.historialCurso hc
        JOIN hm.materiaCurso mc
        JOIN hc.alumno a
        JOIN mc.materia m
        JOIN mc.curso cur
        WHERE YEAR(hc.cicloLectivo.fechaDesde) = :cicloLectivoAnio
        AND hc.fechaHasta IS NULL
        AND hm.estado IN ('CURSANDO', 'EXAMEN', 'COLOQUIO', 'PENDIENTE_EXAMEN')
        AND cur.id = :cursoId
        ORDER BY a.id, m.id, c.etapa, c.numeroNota
    """)
    List<Calificacion> findCalificacionesParaAnalisisConsecutivoPorCurso(@Param("cicloLectivoAnio") Integer cicloLectivoAnio, 
                                                                         @Param("cursoId") Long cursoId);

    // Consulta para obtener el historial completo de calificaciones de un alumno
    @Query("""
        SELECT c FROM Calificacion c
        JOIN c.historialMateria hm
        JOIN hm.historialCurso hc
        JOIN hc.alumno a
        JOIN hc.cicloLectivo cl
        JOIN hm.materiaCurso mc
        JOIN mc.materia m
        WHERE a.id = :alumnoId
        ORDER BY cl.fechaDesde, hc.curso.anio, m.nombre, c.etapa, c.numeroNota
    """)
    List<Calificacion> findHistorialCompletoByAlumnoId(@Param("alumnoId") Long alumnoId);

    // Consulta para obtener historial de un alumno en un ciclo específico
    @Query("""
        SELECT c FROM Calificacion c
        JOIN c.historialMateria hm
        JOIN hm.historialCurso hc
        JOIN hc.alumno a
        JOIN hc.cicloLectivo cl
        JOIN hm.materiaCurso mc
        JOIN mc.materia m
        WHERE a.id = :alumnoId AND cl.id = :cicloLectivoId
        ORDER BY hc.curso.anio, m.nombre, c.etapa, c.numeroNota
    """)
    List<Calificacion> findHistorialByAlumnoAndCiclo(@Param("alumnoId") Long alumnoId, 
                                                     @Param("cicloLectivoId") Long cicloLectivoId);
}
