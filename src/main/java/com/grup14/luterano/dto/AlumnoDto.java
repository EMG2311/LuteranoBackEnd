package com.grup14.luterano.dto;

import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@SuperBuilder@Data@NoArgsConstructor@AllArgsConstructor

public class AlumnoDto extends PersonaDto {
    @NotNull(message = "Es obligatorio asignar un curso al alumno")
    private CursoDto cursoActual;
    private EstadoAlumno estado;
   private TutorDto tutor;
    private List<HistorialCursoDto> historialCursos;

}
