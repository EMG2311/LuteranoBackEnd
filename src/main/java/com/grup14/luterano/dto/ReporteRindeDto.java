package com.grup14.luterano.dto;

import com.grup14.luterano.entities.enums.CondicionRinde;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRindeDto {
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;

    private Long materiaId;
    private String materiaNombre;

    private Double e1;
    private Double e2;
    private Double pg;

    private Integer co;
    private Integer ex;
    private Double pf;

    private CondicionRinde condicion;
    private String estadoAcademico; // PROMOCIONADO, APROBADO_MESA, DEBE_RENDIR
}
