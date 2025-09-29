package com.grup14.luterano.repository;

import com.grup14.luterano.entities.MesaExamenAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaExamenAlumnoRepository extends JpaRepository<MesaExamenAlumno,Long> {
}
