package com.grup14.luterano.repository;

import com.grup14.luterano.entities.HistorialMateria;
import com.grup14.luterano.entities.enums.EstadoMateriaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialMateriaRepository extends JpaRepository<HistorialMateria, Long> {
    Optional<HistorialMateria> findByHistorialCurso_IdAndMateriaCurso_Id(Long historialCursoId, Long materiaCursoId);

    boolean existsByHistorialCurso_Alumno_IdAndMateriaCurso_Materia_Id(Long alumnoId, Long materiaId);

    @Query("""
              select hm from HistorialMateria hm
              where hm.historialCurso.id = :historialCursoId
            """)
    List<HistorialMateria> findAllByHistorialCursoId(@Param("historialCursoId") Long historialCursoId);
    List<HistorialMateria> findByMateriaCurso_IdAndEstado(Long materiaCursoId, EstadoMateriaAlumno estado);
    @Transactional
    @Modifying
    void deleteByHistorialCurso_Id(Long historialCursoId);

    boolean existsByMateriaCurso_Id(Long materiaCursoId);


}
