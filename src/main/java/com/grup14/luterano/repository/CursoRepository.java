package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso,Long> {
    Optional<Curso> findById(Long id);
    Optional<Curso> findByAnio (Integer anio);
    Optional<Curso> findByDivision (Division division);
    Optional<Curso> findByNivel (Nivel nivel);
    Optional<Curso> findByAnioAndDivisionAndNivel(Integer anio, Division division, Nivel nivel);
    @Query("select distinct mc.curso from MateriaCurso mc where mc.docente.id = :docenteId")
    List<Curso> findCursosByDocenteId(@Param("docenteId") Long docenteId);
    List<Curso> findByPreceptorId(Long preceptorId);

}
