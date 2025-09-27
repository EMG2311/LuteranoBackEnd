package com.grup14.luterano.dto;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder(toBuilder=true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InasistenciaAlumnoDto {
    private Long id;
    private LocalDate fecha;
    private EstadoAsistencia estado;
   // private Long alumnoId;
    private Long alumnoId;
    private Long usuarioId;
}
