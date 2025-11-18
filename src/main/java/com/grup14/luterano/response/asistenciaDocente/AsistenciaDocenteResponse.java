package com.grup14.luterano.response.asistenciaDocente;

import com.grup14.luterano.dto.asistencia.AsistenciaDocenteDto;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaDocenteResponse {
    private AsistenciaDocenteDto asistenciaDocenteDto;
    private Integer code;
    private String mensaje;
}
