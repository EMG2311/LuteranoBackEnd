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
public interface CalificacionRepository extends JpaRepository<Calificacion,Long> {
    List<Calificacion> findByHistorialMateria_Id(Long historialMateriaId);

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

}
