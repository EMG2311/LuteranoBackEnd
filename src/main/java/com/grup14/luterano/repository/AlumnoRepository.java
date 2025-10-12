package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno,Long> , JpaSpecificationExecutor<Alumno> {

    Optional<Alumno> findByDni(String dni);
    Optional<Alumno> findByNombre (String nombre);
    Optional<Alumno> findByApellido (String apellido);
    List<Alumno> findByTutor_IdAndEstadoNot(Long tutorId, EstadoAlumno estado);

}
