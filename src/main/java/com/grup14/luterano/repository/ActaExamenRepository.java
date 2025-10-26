package com.grup14.luterano.repository;

import com.grup14.luterano.entities.ActaExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActaExamenRepository extends JpaRepository<ActaExamen,Long> {
    Optional<ActaExamen> findByMesa_Id(Long mesaId);

    Optional<ActaExamen> findByNumeroActa(String numeroActa);

    @Query("""
      select a from ActaExamen a
      join a.mesa m
      where a.numeroActa like %:q%
      order by a.fechaCierre desc
    """)
    List<ActaExamen> searchByNumeroLike(String q);

    @Query("""
      select a from ActaExamen a
      join a.mesa m
      where m.turno.id = :turnoId
      order by a.fechaCierre desc, a.id desc
    """)
    List<ActaExamen> listarPorTurno(Long turnoId);

    @Query("""
      select a from ActaExamen a
      join a.mesa m
      join m.materiaCurso mc
      where mc.curso.id = :cursoId
      order by a.fechaCierre desc, a.id desc
    """)
    List<ActaExamen> listarPorCurso(Long cursoId);

    @Query("""
      select a from ActaExamen a
      where a.fechaCierre between :desde and :hasta
      order by a.fechaCierre desc, a.id desc
    """)
    List<ActaExamen> listarEntreFechas(LocalDate desde, LocalDate hasta);
}
