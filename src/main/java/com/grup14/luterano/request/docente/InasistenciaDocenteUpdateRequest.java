package com.grup14.luterano.request.docente;

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
public class InasistenciaDocenteUpdateRequest {

    private Long id;
    private LocalDate fecha;
    private EstadoAsistencia estado;

}
