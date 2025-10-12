package com.grup14.luterano.repository;

import com.grup14.luterano.entities.AsistenciaDocente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AsistenciaDocenteRepository extends JpaRepository<AsistenciaDocente, Long> {

    Optional<AsistenciaDocente> findByDocente_IdAndFecha(Long docenteId, LocalDate fecha);
}
