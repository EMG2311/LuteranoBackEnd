package com.grup14.luterano.request.alumno;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InasistenciaAlumnoUpdateRequest {

    private Long id;
    private LocalDate fecha;
    private EstadoAsistencia estado;
}
