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
}
