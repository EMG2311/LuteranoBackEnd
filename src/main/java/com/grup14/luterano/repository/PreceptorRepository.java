package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PreceptorRepository extends JpaRepository<Preceptor,Long> {
    Optional<Preceptor> findByDni(String dni);
    Optional<Preceptor> findByEmail(String email);
    Optional<Preceptor> findByIdAndActiveIsTrue(Long id);

    Optional<Preceptor> findByEmailAndActiveIsTrue(String email);
    Optional<Preceptor> findByDniAndActiveIsTrue(String dni);

    Optional<Preceptor> findByEmailAndActiveIsFalse(String email);

    List<Preceptor> findByActiveIsTrue();
}
