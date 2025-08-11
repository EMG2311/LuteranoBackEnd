package com.grup14.luterano.response.alumno;


import com.grup14.luterano.dto.AlumnoDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlumnoResponse {

    // para responder ante una solicitud HTTP (POST) de creación.
    private AlumnoDto alumno;
    private Integer code;
    private String mensaje;

}
