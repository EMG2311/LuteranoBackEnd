package com.grup14.luterano.repository;

import com.grup14.luterano.entities.TurnoExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TurnoExamenRepository extends JpaRepository<TurnoExamen, Long> {
    Optional<TurnoExamen> findByNombre(String nombre);
    List<TurnoExamen> findByAnioOrderByFechaInicioAsc(Integer anio);
    @Query("""
        select (count(t) > 0)
        from TurnoExamen t
        where t.anio = :anio
          and not (t.fechaFin < :desde or t.fechaInicio > :hasta)
    """)
    boolean existsOverlappingInYear(Integer anio, LocalDate desde, LocalDate hasta);
    @Query("""
    select (count(t) > 0)
    from TurnoExamen t
    where t.id <> :turnoId
      and t.anio = :anio
      and not (t.fechaFin < :desde or t.fechaInicio > :hasta)
""")
    boolean existsOverlappingInYearExcludingId(Long turnoId, Integer anio, LocalDate desde, LocalDate hasta);
}