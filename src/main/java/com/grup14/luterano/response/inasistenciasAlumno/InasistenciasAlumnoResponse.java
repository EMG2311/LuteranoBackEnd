package com.grup14.luterano.response.inasistenciasAlumno;

import com.grup14.luterano.dto.inasistenciasAlumno.InasistenciaAlumnoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InasistenciasAlumnoResponse {

    private List<InasistenciaAlumnoDto> inasistencias;
    private Long alumnoId;
    private String nombreCompleto;
    private Integer totalInasistencias;
    private Integer code;
    private String mensaje;
}