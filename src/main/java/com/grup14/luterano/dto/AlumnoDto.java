package com.grup14.luterano.dto;

import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@SuperBuilder@Data@NoArgsConstructor@AllArgsConstructor

public class AlumnoDto extends PersonaDto {
    // Campos de Persona ya esta en extendida PersonaDto

    // Campos específicos de Alumno
    private Curso cursoActual;  /// no deberia ser CursoDto?
    private EstadoAlumno estado;
   private Tutor tutor; ///
    private List<HistorialCurso> historialCursos = new ArrayList<>();

    // incluir entity de inasistenciaAlumno?
}
