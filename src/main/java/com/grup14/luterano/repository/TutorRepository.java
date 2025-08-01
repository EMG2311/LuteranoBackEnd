package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor,Integer> {

    Optional<Tutor> findByNombre (String nombre);
}