package com.grup14.luterano.dto;

import com.grup14.luterano.entities.enums.EstadoConvocado;
import lombok.Builder;
import lombok.Data;


@Data @Builder
public class MesaExamenAlumnoDto {
    private Long id;           // MesaExamenAlumno.id
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;

    private EstadoConvocado estado;
    private Integer notaFinal;

    private Long turnoId;
    private String turnoNombre;
}
