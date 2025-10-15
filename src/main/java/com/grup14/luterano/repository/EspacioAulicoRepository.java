package com.grup14.luterano.repository;

import com.grup14.luterano.entities.EspacioAulico;
import com.grup14.luterano.entities.HorarioClaseModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspacioAulicoRepository extends JpaRepository<EspacioAulico,Long> {

    // Metodo para verificar si ya existe un espacio con ese nombre
    Optional<EspacioAulico> findByNombreIgnoreCase(String nombre);

}
