package com.grup14.luterano.repository;

import com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto;
import com.grup14.luterano.entities.AsistenciaAlumno;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaAlumnoRepository extends JpaRepository<AsistenciaAlumno, Long> {

    Optional<AsistenciaAlumno> findByAlumno_IdAndFecha(Long alumnoId, LocalDate fecha);

    List<AsistenciaAlumno> findByAlumno_CursoActual_IdAndFecha(Long cursoId, LocalDate fecha);

    @Query("""
        select aa
        from AsistenciaAlumno aa
        join fetch aa.alumno a
        where a.id = :alumnoId
          and aa.fecha between :desde and :hasta
        order by aa.fecha
        """)
    List<AsistenciaAlumno> findDetallePorAlumnoEntreFechas(@Param("alumnoId") Long alumnoId,
                                                           @Param("desde") LocalDate desde,
                                                           @Param("hasta") LocalDate hasta);

    @Query("""
        select aa
        from AsistenciaAlumno aa
        join fetch aa.alumno a
        join fetch a.cursoActual c
        where c.id = :cursoId
          and aa.fecha between :desde and :hasta
        order by a.apellido, a.nombre, aa.fecha
        """)
    List<AsistenciaAlumno> findDetallePorCursoEntreFechas(@Param("cursoId") Long cursoId,
                                                          @Param("desde") LocalDate desde,
                                                          @Param("hasta") LocalDate hasta);

    // =========================
    //  TARDANZAS POR CURSO (>5)
    // =========================

    @Query("""
        select new com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto(
            a.id,
            a.apellido,
            a.nombre,
            a.dni,
            c.id,
            c.anio,
            c.division,
            count(aa)
        )
        from AsistenciaAlumno aa
            join aa.alumno a
            join a.cursoActual c
        where c.id = :cursoId
          and aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and (:desde is null or aa.fecha >= :desde)
          and (:hasta is null or aa.fecha <= :hasta)
        group by a.id, a.apellido, a.nombre, a.dni,
                 c.id, c.anio, c.division
        having count(aa) > 5
        order by c.anio, c.division, a.apellido, a.nombre
        """)
    List<TardanzaRowDto> tardanzasPorCurso(@Param("cursoId") Long cursoId,
                                           @Param("desde") LocalDate desde,
                                           @Param("hasta") LocalDate hasta);

    @Query("""
        select new com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto(
            a.id,
            a.apellido,
            a.nombre,
            a.dni,
            c.id,
            c.anio,
            c.division,
            count(aa)
        )
        from AsistenciaAlumno aa
            join aa.alumno a
            join a.cursoActual c
        where c.id = :cursoId
          and aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and (:desde is null or aa.fecha >= :desde)
          and (:hasta is null or aa.fecha <= :hasta)
        group by a.id, a.apellido, a.nombre, a.dni,
                 c.id, c.anio, c.division
        having count(aa) > 5
        order by c.anio, c.division, a.apellido, a.nombre
        """)
    List<TardanzaRowDto> tardanzasPorCurso(@Param("cursoId") Long cursoId,
                                           @Param("desde") LocalDate desde,
                                           @Param("hasta") LocalDate hasta,
                                           Pageable pageable);


    // =========================
    //  TARDANZAS TODOS LOS CURSOS (>5)
    // =========================

    @Query("""
        select new com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto(
            a.id,
            a.apellido,
            a.nombre,
            a.dni,
            c.id,
            c.anio,
            c.division,
            count(aa)
        )
        from AsistenciaAlumno aa
            join aa.alumno a
            join a.cursoActual c
        where aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and (:desde is null or aa.fecha >= :desde)
          and (:hasta is null or aa.fecha <= :hasta)
        group by a.id, a.apellido, a.nombre, a.dni,
                 c.id, c.anio, c.division
        having count(aa) > 5
        order by c.anio, c.division, a.apellido, a.nombre
        """)
    List<TardanzaRowDto> tardanzasTodosCursos(@Param("desde") LocalDate desde,
                                              @Param("hasta") LocalDate hasta);

    @Query("""
        select new com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto(
            a.id,
            a.apellido,
            a.nombre,
            a.dni,
            c.id,
            c.anio,
            c.division,
            count(aa)
        )
        from AsistenciaAlumno aa
            join aa.alumno a
            join a.cursoActual c
        where aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and (:desde is null or aa.fecha >= :desde)
          and (:hasta is null or aa.fecha <= :hasta)
        group by a.id, a.apellido, a.nombre, a.dni,
                 c.id, c.anio, c.division
        having count(aa) > 5
        order by c.anio, c.division, a.apellido, a.nombre
        """)
    List<TardanzaRowDto> tardanzasTodosCursos(@Param("desde") LocalDate desde,
                                              @Param("hasta") LocalDate hasta,
                                              Pageable pageable);


    // =========================
    //  INASISTENCIAS / ESTADÍSTICAS
    // =========================

    @Query("""
        select aa.alumno.id,
               sum(
                   case
                       when aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.AUSENTE
                            or aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.JUSTIFICADO
                            or aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.CON_LICENCIA
                           then 1.0
                       when aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
                            or aa.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.RETIRO
                           then 0.25
                       else 0.0
                   end
               )
        from AsistenciaAlumno aa
        where aa.fecha between :desde and :hasta
          and aa.alumno.id in :alumnoIds
        group by aa.alumno.id
        """)
    List<Object[]> sumarInasistenciasPorAlumnoEntreFechas(@Param("desde") LocalDate desde,
                                                          @Param("hasta") LocalDate hasta,
                                                          @Param("alumnoIds") List<Long> alumnoIds);

    @Query("""
        select aa.alumno.id, count(aa)
        from AsistenciaAlumno aa
        where aa.fecha between :desde and :hasta
          and aa.alumno.id in :alumnoIds
        group by aa.alumno.id
        """)
    List<Object[]> contarRegistrosPorAlumnoEntreFechas(@Param("desde") LocalDate desde,
                                                       @Param("hasta") LocalDate hasta,
                                                       @Param("alumnoIds") List<Long> alumnoIds);

    @Query("""
        select a.estado, count(a)
        from AsistenciaAlumno a
        where a.alumno.id = :alumnoId
          and a.fecha between :desde and :hasta
        group by a.estado
        """)
    List<Object[]> contarPorEstadoEntreFechas(@Param("alumnoId") Long alumnoId,
                                              @Param("desde") LocalDate desde,
                                              @Param("hasta") LocalDate hasta);

    @Query("""
        select a 
        from AsistenciaAlumno a
        left join fetch a.alumno al
        left join fetch al.cursoActual c
        where a.alumno.id = :alumnoId
        order by a.fecha desc
        """)
    List<AsistenciaAlumno> findByAlumnoIdWithCurso(@Param("alumnoId") Long alumnoId);

    long deleteByAlumno_Id(Long alumnoId);

    long countByAlumno_Id(Long alumnoId);

    /**
     * Tardanzas de un alumno en un rango de fechas (para detalle).
     */
    @Query("""
        select a
        from AsistenciaAlumno a
        where a.alumno.id = :alumnoId
          and a.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha <= :hasta)
        order by a.fecha desc
        """)
    List<AsistenciaAlumno> findTardanzasPorAlumno(@Param("alumnoId") Long alumnoId,
                                                  @Param("desde") LocalDate desde,
                                                  @Param("hasta") LocalDate hasta);
}
