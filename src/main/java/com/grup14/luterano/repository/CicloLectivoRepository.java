package com.grup14.luterano.repository;

import com.grup14.luterano.entities.CicloLectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CicloLectivoRepository extends JpaRepository<CicloLectivo,Long> {

        // Metodo clave: Busca el último ciclo lectivo (el más reciente)
        Optional<CicloLectivo> findTopByOrderByFechaHastaDesc();

        // Metodo para validar que no exista un ciclo para ese año
        boolean existsByNombre(String nombre);

}
