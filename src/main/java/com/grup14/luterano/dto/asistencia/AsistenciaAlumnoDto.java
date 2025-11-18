package com.grup14.luterano.dto;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaAlumnoDto {
    private Long id;
    private Long alumnoId;
    private String alumnoNombre;
    private String alumnoApellido;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String observacion;
}
