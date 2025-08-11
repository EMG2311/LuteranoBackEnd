package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno,Long> {

    Optional<Alumno> findByDni(String dni);
    Optional<Alumno> findByNombre (String nombre);
    Optional<Alumno> findByApellido (String apellido);

}
