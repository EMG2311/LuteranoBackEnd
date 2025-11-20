package com.grup14.luterano.response;

import com.grup14.luterano.dto.mesaExamen.AlumnoDebeMateriaDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AlumnosDebenMateriaResponse {
    private int code;
    private String mensaje;
    private int total;
    private List<AlumnoDebeMateriaDto> alumnos;
}