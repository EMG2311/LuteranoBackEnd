package com.grup14.luterano.repository;

import com.grup14.luterano.entities.MesaExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface MesaExamenRepository extends JpaRepository<MesaExamen,Long> {

    boolean existsByAulaId(Long aulaId);

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
}
