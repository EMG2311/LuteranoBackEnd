package com.grup14.luterano.response.CursoAlumno;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.dto.CursoDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoAlumnosResponse {
    private CursoDto curso;
    private List<AlumnoDto> alumnos;
    private Integer code;
    private String mensaje;
}
