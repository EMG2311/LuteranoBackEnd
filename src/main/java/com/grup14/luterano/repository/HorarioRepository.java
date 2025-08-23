package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.enums.DiaSemana;
import com.grup14.luterano.entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HorarioRepository extends JpaRepository<Horario,Long> {
    List<Horario> findByDocenteAndDiaSemana(Docente docente, DiaSemana diaSemana);
}
