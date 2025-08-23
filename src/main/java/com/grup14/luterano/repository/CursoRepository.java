package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CursoRepository extends JpaRepository<Curso,Long> {
    Optional<Curso> findById(Long id);
    Optional<Curso> findByNumero (Integer numero);
    Optional<Curso> findByDivision (Division division);
    Optional<Curso> findByNivel (Nivel nivel);
    Optional<Curso> findByNumeroAndDivisionAndNivel(Integer numero, Division division, Nivel nivel);

}
