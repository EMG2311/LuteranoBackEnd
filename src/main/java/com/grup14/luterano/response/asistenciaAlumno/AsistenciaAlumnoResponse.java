package com.grup14.luterano.response.asistenciaAlumno;

import com.grup14.luterano.dto.asistencia.AsistenciaAlumnoDto;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaAlumnoResponse {
    private AsistenciaAlumnoDto asistenciaAlumnoDto;
    private Integer code;
    private String mensaje;
}
