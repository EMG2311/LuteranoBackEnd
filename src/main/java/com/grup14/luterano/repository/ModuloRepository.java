package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Modulo;
import com.grup14.luterano.entities.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    Optional<Modulo> findByOrden(int orden);

    List<Modulo> findAllByOrderByOrdenAsc();

    @Query("""
                select m
                from Modulo m
                where not exists (
                    select 1
                    from HorarioClaseModulo h
                    where h.materiaCurso.curso.id = :cursoId
                      and h.diaSemana = :dia
                      and h.modulo = m
                )
                order by m.orden asc
            """)
    List<Modulo> findModulosLibresPorCursoYDia(@Param("cursoId") Long cursoId, @Param("dia") DiaSemana dia);
}
