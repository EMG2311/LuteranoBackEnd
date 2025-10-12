package com.grup14.luterano.request;


import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaAlumnoBulkRequest {
    private Long cursoId;
    private LocalDate fecha;

    // UI típica: pasan los presentes; el resto del curso queda AUSENTE
    private List<Long> presentesIds;

    // Opcionales “excepciones” (sobrescriben lo anterior)
    // e.g. {"12":"TARDE","18":"JUSTIFICADO"}
    private Map<Long, EstadoAsistencia> overridesPorAlumnoId;
}