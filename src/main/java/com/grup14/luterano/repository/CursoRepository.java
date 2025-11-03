package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    Optional<Curso> findById(Long id);

    Optional<Curso> findByAnio(Integer anio);

    Optional<Curso> findByDivision(Division division);

    Optional<Curso> findByNivel(Nivel nivel);

    Optional<Curso> findByAnioAndDivision(Integer anio, Division division);

    Optional<Curso> findByAnioAndDivisionAndNivel(Integer anio, Division division, Nivel nivel);

    List<Curso> findByPreceptor_Id(Long preceptorId);

    @Query("""
                select distinct c
                from Curso c
                join c.dictados mc
                where mc.docente.id = :docenteId
            """)
    List<Curso> findByDocente_Id(@Param("docenteId") Long docenteId);
}
