package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente,Integer> {
    Optional<Docente> findByEmail(String email);
    Optional<Docente> findByDni(String dni);
}
