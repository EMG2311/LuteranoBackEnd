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
public interface HistorialCursoRepository extends JpaRepository<HistorialCurso,Long> {
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
}
