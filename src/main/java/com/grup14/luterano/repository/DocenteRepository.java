package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente,Long> {
    Optional<Docente> findByEmail(String email);
    Optional<Docente> findByDni(String dni);
    Optional<Docente> findByUser(User user);
}
