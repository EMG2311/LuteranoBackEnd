package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.HorarioClaseModulo;
import com.grup14.luterano.entities.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HorarioClaseModuloRepository  extends JpaRepository<HorarioClaseModulo,Long> {


    List<HorarioClaseModulo> findByMateriaCurso_Curso_IdOrderByDiaSemanaAscModulo_OrdenAsc(Long cursoId);


    List<HorarioClaseModulo> findByMateriaCurso_Curso_IdAndMateriaCurso_Materia_IdOrderByDiaSemanaAscModulo_OrdenAsc(
            Long cursoId, Long materiaId);


    boolean existsByMateriaCurso_Curso_IdAndDiaSemanaAndModulo_Id(Long cursoId, DiaSemana dia, Long moduloId);


    boolean existsByMateriaCurso_Docente_IdAndDiaSemanaAndModulo_Id(Long docenteId, DiaSemana dia, Long moduloId);


    List<HorarioClaseModulo> findByMateriaCurso_Curso_IdAndDiaSemana(Long cursoId, DiaSemana dia);
    int deleteByMateriaCurso_IdAndDiaSemanaAndModulo_Id(Long materiaCursoId, DiaSemana dia, Long moduloId);
}
