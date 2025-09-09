package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor,Long> {

    Optional<Tutor> findByNombre (String nombre);
    Optional<Tutor> findByDni(String dni);
    Optional<Tutor> findByEmail(String email);
}