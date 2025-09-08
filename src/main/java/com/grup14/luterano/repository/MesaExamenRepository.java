package com.grup14.luterano.repository;

import com.grup14.luterano.entities.MesaExamen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MesaExamenRepository extends JpaRepository<MesaExamen,Long> {

    boolean existsByAulaId(Long aulaId);

}
