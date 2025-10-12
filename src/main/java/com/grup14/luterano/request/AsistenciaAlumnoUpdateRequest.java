package com.grup14.luterano.request;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaAlumnoUpdateRequest {
    private Long alumnoId;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String observacion;
}
