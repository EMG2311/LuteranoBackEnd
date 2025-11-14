package com.grup14.luterano.repository;

import com.grup14.luterano.entities.HistorialCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialCursoRepository extends JpaRepository<HistorialCurso, Long> {
    Optional<HistorialCurso> findByAlumno_IdAndCicloLectivo_IdAndFechaHastaIsNull(Long alumnoId, Long cicloLectivoId);

    List<HistorialCurso> findByAlumno_IdAndCicloLectivo_IdAndCurso_Id(Long alumnoId, Long cicloLectivoId, Long cursoId);

    List<HistorialCurso> findByAlumno_IdAndCicloLectivo_Id(Long alumnoId, Long cicloLectivoId);

    List<HistorialCurso> findByAlumno_IdAndCurso_Id(Long alumnoId, Long cursoId);

    List<HistorialCurso> findByAlumno_Id(Long alumnoId);

    Optional<HistorialCurso> findByAlumno_IdAndFechaHastaIsNull(Long alumnoId);

    @Query("""
              select hc
              from HistorialCurso hc
              where hc.alumno.id = :alumnoId
                and hc.cicloLectivo.id = :cicloId
                and hc.fechaDesde <= :fecha
                and (hc.fechaHasta is null or hc.fechaHasta >= :fecha)
              order by hc.fechaDesde desc
            """)
    Optional<HistorialCurso> findVigenteEnFecha(Long alumnoId, Long cicloId, LocalDate fecha);

    @Query("""
              select (count(mc) > 0)
              from HistorialCurso hc
                join hc.curso c
                join c.dictados mc
              where hc.alumno.id = :alumnoId
                and mc.materia.id = :materiaId
            """)
    boolean existsAlumnoCursoMateria(@Param("alumnoId") Long alumnoId,
                                     @Param("materiaId") Long materiaId);


    @Query("""
               select hc
               from HistorialCurso hc
               join fetch hc.alumno a
               where hc.curso.id = :cursoId
                 and hc.cicloLectivo.id = :cicloId
                 and hc.fechaHasta is null
               order by a.apellido asc, a.nombre asc
            """)
    List<HistorialCurso> findAbiertosByCursoAndCiclo(
            @Param("cursoId") Long cursoId,
            @Param("cicloId") Long cicloId
    );


    @Query("""
               select hc
               from HistorialCurso hc
               join fetch hc.alumno a
               join fetch hc.curso c
               where hc.cicloLectivo.id = :cicloId
                 and hc.fechaHasta is null
               order by c.anio asc, c.nivel asc, c.division asc, a.apellido asc, a.nombre asc
            """)
    List<HistorialCurso> findAbiertosByCiclo(@Param("cicloId") Long cicloId);

    // Para reactivación de alumnos: obtener historiales ordenados por fecha
    List<HistorialCurso> findByAlumno_IdOrderByFechaDesdeDesc(Long alumnoId);

    // Métodos para ranking por promedio
    @Query("""
                select hc
                from HistorialCurso hc
                join fetch hc.alumno a
                where hc.curso.id = :cursoId
                  and hc.cicloLectivo.id = :cicloId
                  and hc.fechaHasta is null
                  and hc.promedio is not null
                order by hc.promedio desc, a.apellido asc, a.nombre asc
            """)
    List<HistorialCurso> findRankingByCursoAndCiclo(
            @Param("cursoId") Long cursoId,
            @Param("cicloId") Long cicloId
    );

    @Query("""
                select hc
                from HistorialCurso hc
                join fetch hc.alumno a
                join fetch hc.curso c
                where hc.cicloLectivo.id = :cicloId
                  and hc.fechaHasta is null
                  and hc.promedio is not null
                order by hc.promedio desc, a.apellido asc, a.nombre asc
            """)
    List<HistorialCurso> findRankingByCiclo(@Param("cicloId") Long cicloId);

    // Método para obtener historiales activos sin filtrar por promedio (para ranking dinámico)
    @Query("""
                select hc
                from HistorialCurso hc
                join fetch hc.alumno a
                where hc.curso.id = :cursoId
                  and hc.cicloLectivo.id = :cicloId
                  and hc.fechaHasta is null
                order by a.apellido asc, a.nombre asc
            """)
    List<HistorialCurso> findHistorialesActivosParaRanking(
            @Param("cursoId") Long cursoId,
            @Param("cicloId") Long cicloId
    );

    @Query("""
                select hc
                from HistorialCurso hc
                join fetch hc.alumno a
                join fetch hc.curso c
                where hc.cicloLectivo.id = :cicloId
                  and hc.fechaHasta is null
                order by a.apellido asc, a.nombre asc
            """)
    List<HistorialCurso> findHistorialesActivosParaRankingTodos(@Param("cicloId") Long cicloId);

    @Query("""
                select distinct c
                from HistorialCurso hc
                join hc.curso c
                where hc.cicloLectivo.id = :cicloId
                  and hc.fechaHasta is null
                order by c.anio asc, c.nivel asc, c.division asc
            """)
    List<com.grup14.luterano.entities.Curso> findCursosActivosByCiclo(@Param("cicloId") Long cicloId);

    // Para reporte de desempeño docente: obtener IDs de alumnos por curso y ciclo
    @Query("""
                SELECT hc.alumno.id
                FROM HistorialCurso hc
                WHERE hc.curso.id = :cursoId
                AND YEAR(hc.cicloLectivo.fechaDesde) = :cicloLectivoAnio
            """)
    List<Long> findAlumnosIdsPorCursoYCiclo(@Param("cursoId") Long cursoId,
                                            @Param("cicloLectivoAnio") Integer cicloLectivoAnio);

    // Consulta para obtener todo el historial curso de un alumno
    @Query("""
        SELECT hc FROM HistorialCurso hc
        JOIN FETCH hc.cicloLectivo cl
        JOIN FETCH hc.curso c
        WHERE hc.alumno.id = :alumnoId
        ORDER BY cl.fechaDesde, c.anio
    """)
    List<HistorialCurso> findHistorialCompletoByAlumnoId(@Param("alumnoId") Long alumnoId);

}
