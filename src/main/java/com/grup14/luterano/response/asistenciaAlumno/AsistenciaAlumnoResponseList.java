package com.grup14.luterano.response.asistenciaAlumno;

import com.grup14.luterano.dto.AsistenciaAlumnoDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaAlumnoResponseList {
    private List<AsistenciaAlumnoDto> items;
    private Integer code;
    private String mensaje;
}