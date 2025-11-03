package com.grup14.luterano.repository;
import com.grup14.luterano.entities.MateriaCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaCursoRepository extends JpaRepository<MateriaCurso,Long> {
    List<MateriaCurso>findByMateriaId(Long idMateria);
    boolean existsByMateriaIdAndCursoId(Long materiaId, Long cursoId);
    Optional<MateriaCurso> findByMateriaIdAndCursoId(Long materiaId, Long cursoId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByCursoId(Long id);
    List<MateriaCurso> findByCursoId(Long cursoId);
    @Query("""
      select mc from MateriaCurso mc
      join mc.materia m
      where mc.curso.id = :cursoId and lower(m.nombre) = lower(:materiaNombre)
    """)
    Optional<MateriaCurso> findByCursoAndMateriaNombre(@Param("cursoId") Long cursoId, @Param("materiaNombre") String materiaNombre);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update MateriaCurso mc
        set mc.docente = null
        where mc.docente.id = :docenteId
    """)
    int unassignDocenteFromAll(@Param("docenteId") Long docenteId);

  // Para disponibilidad docente: materias dictadas por un docente
  java.util.List<MateriaCurso> findByDocente_Id(Long docenteId);

  // Para reporte de desempeño docente
  @Query("""
    SELECT mc.id as materiaCursoId,
           m.id as materiaId,
           m.nombre as nombreMateria,
           d.id as docenteId,
           d.apellido as apellidoDocente,
           d.nombre as nombreDocente,
           c.id as cursoId,
           c.anio as anio,
           c.nivel as nivel,
           c.division as division
    FROM MateriaCurso mc
    JOIN mc.materia m
    JOIN mc.curso c
    JOIN mc.docente d
    WHERE EXISTS (
        SELECT 1 FROM HistorialCurso hc
        WHERE hc.curso.id = c.id
        AND hc.cicloLectivo.anio = :cicloLectivoAnio
    )
    ORDER BY m.nombre, d.apellido, c.anio, c.division
  """)
  List<Object[]> findMateriasConDocentePorCiclo(@Param("cicloLectivoAnio") Integer cicloLectivoAnio);

  @Query("""
    SELECT mc.id as materiaCursoId,
           m.id as materiaId,
           m.nombre as nombreMateria,
           d.id as docenteId,
           d.apellido as apellidoDocente,
           d.nombre as nombreDocente,
           c.id as cursoId,
           c.anio as anio,
           c.nivel as nivel,
           c.division as division
    FROM MateriaCurso mc
    JOIN mc.materia m
    JOIN mc.curso c
    JOIN mc.docente d
    WHERE m.id = :materiaId
    AND EXISTS (
        SELECT 1 FROM HistorialCurso hc
        WHERE hc.curso.id = c.id
        AND hc.cicloLectivo.anio = :cicloLectivoAnio
    )
    ORDER BY d.apellido, c.anio, c.division
  """)
  List<Object[]> findMateriasConDocentePorCicloYMateria(@Param("cicloLectivoAnio") Integer cicloLectivoAnio, 
                                                       @Param("materiaId") Long materiaId);

  @Query("""
    SELECT mc.id as materiaCursoId,
           m.id as materiaId,
           m.nombre as nombreMateria,
           d.id as docenteId,
           d.apellido as apellidoDocente,
           d.nombre as nombreDocente,
           c.id as cursoId,
           c.anio as anio,
           c.nivel as nivel,
           c.division as division
    FROM MateriaCurso mc
    JOIN mc.materia m
    JOIN mc.curso c
    JOIN mc.docente d
    WHERE d.id = :docenteId
    AND EXISTS (
        SELECT 1 FROM HistorialCurso hc
        WHERE hc.curso.id = c.id
        AND hc.cicloLectivo.anio = :cicloLectivoAnio
    )
    ORDER BY m.nombre, c.anio, c.division
  """)
  List<Object[]> findMateriasConDocentePorCicloYDocente(@Param("cicloLectivoAnio") Integer cicloLectivoAnio, 
                                                       @Param("docenteId") Long docenteId);
}
