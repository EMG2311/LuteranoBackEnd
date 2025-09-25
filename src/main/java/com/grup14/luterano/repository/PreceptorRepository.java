package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreceptorRepository extends JpaRepository<Preceptor,Long> {
    Optional<Preceptor> findByDni(String dni);
    Optional<Preceptor> findByEmail(String email);
    Optional<Preceptor> findByUser (User usuario);

}
