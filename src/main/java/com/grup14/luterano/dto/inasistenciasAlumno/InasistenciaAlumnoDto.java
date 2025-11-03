package com.grup14.luterano.dto.inasistenciasAlumno;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InasistenciaAlumnoDto {

    private Long asistenciaId;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private Boolean justificada;
    private String observacion;
    private CursoDto curso;
    private String diaSemana;
}