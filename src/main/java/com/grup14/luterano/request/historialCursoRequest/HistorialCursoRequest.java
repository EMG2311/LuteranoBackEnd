package com.grup14.luterano.request.historialCursoRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialCursoRequest {
    private Long alumnoId;
    private Long cursoId;
    private Long cicloLectivoId;
}
