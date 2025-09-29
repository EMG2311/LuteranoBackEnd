package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo,Long> {
}
