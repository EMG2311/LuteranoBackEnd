package com.grup14.luterano.dto;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaDocenteDto {
    private Long id;
    private Long docenteId;
    private String docenteNombre;
    private String docenteApellido;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String observacion;
}