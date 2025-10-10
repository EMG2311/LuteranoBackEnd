package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DocenteRepository extends JpaRepository<Docente,Long> {
    Optional<Docente> findByEmail(String email);
    Optional<Docente> findByDni(String dni);
    Optional<Docente> findByEmailAndActiveIsTrue(String email);
    Optional<Docente> findByDniAndActiveIsTrue(String dni);
    Optional<Docente> findByEmailAndActiveIsFalse(String email);
    Optional<Docente> findByIdAndActiveIsTrue(Long id);
    List<Docente> findByActiveIsTrue();

}
