package com.grup14.luterano.request;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaDocenteUpdateRequest {
    private Long docenteId;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String observacion;
}
