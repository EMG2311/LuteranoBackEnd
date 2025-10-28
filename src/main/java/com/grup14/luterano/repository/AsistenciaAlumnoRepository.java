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
        select new com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto(
            al.id, al.apellido, al.nombre, al.dni,
            c.id, c.anio, c.division,
            count(a)
        )
        from AsistenciaAlumno a
        join a.alumno al
        left join al.cursoActual c
        where a.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha <= :hasta)
          and (al.estado is null or al.estado <> com.grup14.luterano.entities.enums.EstadoAlumno.BORRADO)
        group by al.id, al.apellido, al.nombre, al.dni, c.id, c.anio, c.division
        order by count(a) desc
    """)
    List<TardanzaRowDto> tardanzasTodosCursos(@Param("desde") LocalDate desde,
                                              @Param("hasta") LocalDate hasta);

    @Query("""
        select new com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto(
            al.id, al.apellido, al.nombre, al.dni,
            c.id, c.anio, c.division,
            count(a)
        )
        from AsistenciaAlumno a
        join a.alumno al
        left join al.cursoActual c
        where a.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha <= :hasta)
          and (al.estado is null or al.estado <> com.grup14.luterano.entities.enums.EstadoAlumno.BORRADO)
        group by al.id, al.apellido, al.nombre, al.dni, c.id, c.anio, c.division
        order by count(a) desc
    """)
    List<TardanzaRowDto> tardanzasTodosCursos(@Param("desde") LocalDate desde,
                                              @Param("hasta") LocalDate hasta,
                                              Pageable pageable);

    @Query("""
        select new com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto(
            al.id, al.apellido, al.nombre, al.dni,
            c.id, c.anio, c.division,
            count(a)
        )
        from AsistenciaAlumno a
        join a.alumno al
        join al.cursoActual c
        where a.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and c.id = :cursoId
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha <= :hasta)
          and (al.estado is null or al.estado <> com.grup14.luterano.entities.enums.EstadoAlumno.BORRADO)
        group by al.id, al.apellido, al.nombre, al.dni, c.id, c.anio, c.division
        order by count(a) desc
    """)
    List<TardanzaRowDto> tardanzasPorCurso(@Param("cursoId") Long cursoId,
                                           @Param("desde") LocalDate desde,
                                           @Param("hasta") LocalDate hasta);

    @Query("""
        select new com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto(
            al.id, al.apellido, al.nombre, al.dni,
            c.id, c.anio, c.division,
            count(a)
        )
        from AsistenciaAlumno a
        join a.alumno al
        join al.cursoActual c
        where a.estado = com.grup14.luterano.entities.enums.EstadoAsistencia.TARDE
          and c.id = :cursoId
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha <= :hasta)
          and (al.estado is null or al.estado <> com.grup14.luterano.entities.enums.EstadoAlumno.BORRADO)
        group by al.id, al.apellido, al.nombre, al.dni, c.id, c.anio, c.division
        order by count(a) desc
    """)
    List<TardanzaRowDto> tardanzasPorCurso(@Param("cursoId") Long cursoId,
                                           @Param("desde") LocalDate desde,
                                           @Param("hasta") LocalDate hasta,
                                           Pageable pageable);



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
    List<Object[]> sumarInasistenciasPorAlumnoEntreFechas(
            LocalDate desde, LocalDate hasta, List<Long> alumnoIds
    );

        @Query("""
                select a.estado, count(a)
                from AsistenciaAlumno a
                where a.alumno.id = :alumnoId
                    and a.fecha between :desde and :hasta
                group by a.estado
        """)
        java.util.List<Object[]> contarPorEstadoEntreFechas(@Param("alumnoId") Long alumnoId,
                                                                                                                @Param("desde") LocalDate desde,
                                                                                                                @Param("hasta") LocalDate hasta);

    long deleteByAlumno_Id(Long alumnoId);

    long countByAlumno_Id(Long alumnoId);
}
