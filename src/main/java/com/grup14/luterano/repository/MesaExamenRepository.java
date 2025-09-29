package com.grup14.luterano.repository;

import com.grup14.luterano.entities.MesaExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface MesaExamenRepository extends JpaRepository<MesaExamen,Long> {

    boolean existsByAulaId(Long aulaId);

}
