package com.grup14.luterano.response.alumno;

import com.grup14.luterano.dto.InasistenciaAlumnoDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InasistenciaAlumnoResponse {

    private InasistenciaAlumnoDto InasistenciaAlumno;
    private Integer code;
    private String mensaje;
}
