package com.grup14.luterano.response.alumno;

import com.grup14.luterano.dto.InasistenciaAlumnoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InasistenciaAlumnoResponseList {

    private List<InasistenciaAlumnoDto> inasistenciaAlumnoDtos;
    private Integer code;
    private String mensaje;

}
