package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Desempeno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesempenoRepository extends JpaRepository<Desempeno, Long> {
}
