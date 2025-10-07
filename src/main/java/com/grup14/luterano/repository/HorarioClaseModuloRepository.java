package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.HorarioClaseModulo;
import com.grup14.luterano.entities.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HorarioClaseModuloRepository  extends JpaRepository<HorarioClaseModulo,Long> {


    List<HorarioClaseModulo> findByMateriaCurso_Curso_IdOrderByDiaSemanaAscModulo_OrdenAsc(Long cursoId);
    @Query("""
        select h
        from HorarioClaseModulo h
          join fetch h.materiaCurso mc
          join fetch mc.curso c
          join fetch mc.materia m
        where mc.docente.id = :docenteId
          and h.diaSemana = :dia
          and h.modulo.id = :moduloId
    """)
    List<HorarioClaseModulo> findConflictosDocente(
            @Param("docenteId") Long docenteId,
            @Param("dia") DiaSemana dia,
            @Param("moduloId") Long moduloId
    );

    List<HorarioClaseModulo> findByMateriaCurso_Curso_IdAndMateriaCurso_Materia_IdOrderByDiaSemanaAscModulo_OrdenAsc(
            Long cursoId, Long materiaId);


    boolean existsByMateriaCurso_Curso_IdAndDiaSemanaAndModulo_Id(Long cursoId, DiaSemana dia, Long moduloId);


    boolean existsByMateriaCurso_Docente_IdAndDiaSemanaAndModulo_Id(Long docenteId, DiaSemana dia, Long moduloId);


    List<HorarioClaseModulo> findByMateriaCurso_Curso_IdAndDiaSemana(Long cursoId, DiaSemana dia);
    int deleteByMateriaCurso_IdAndDiaSemanaAndModulo_Id(Long materiaCursoId, DiaSemana dia, Long moduloId);
}
