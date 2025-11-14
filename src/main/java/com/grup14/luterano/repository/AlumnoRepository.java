package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long>, JpaSpecificationExecutor<Alumno> {

    Optional<Alumno> findByDni(String dni);

    Optional<Alumno> findByEmail(String email);

    Optional<Alumno> findByNombre(String nombre);

    Optional<Alumno> findByApellido(String apellido);

    // Método actualizado para la relación many-to-many con tutores
    @Query("SELECT a FROM Alumno a JOIN a.tutores t WHERE t.id = :tutorId AND a.estado != :estado")
    List<Alumno> findByTutores_IdAndEstadoNot(@Param("tutorId") Long tutorId, @Param("estado") EstadoAlumno estado);

    List<Alumno> findByCursoActual_Id(Long cursoId);

    List<Alumno> findByCursoActual_IdAndEstadoNot(Long cursoId, EstadoAlumno estado);

    // Método para obtener alumnos activos de un curso (excluye borrados, egresados, excluidos)
    List<Alumno> findByCursoActual_IdAndEstadoNotIn(Long cursoId, List<EstadoAlumno> estados);

    List<Alumno> findByEstadoNotIn(List<EstadoAlumno> estados);

    List<Alumno> findByEstado(EstadoAlumno estado); // Para consultar egresados específicamente


    //Cuenta el número total de alumnos asignados al curso actual

    long countByCursoActual_Id(Long cursoId);


}
