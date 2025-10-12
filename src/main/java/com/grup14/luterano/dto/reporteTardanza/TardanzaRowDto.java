package com.grup14.luterano.dto.reporteTardanza;

import com.grup14.luterano.entities.enums.Division;
import lombok.*;

@AllArgsConstructor@Getter@Setter@NoArgsConstructor@Builder
public class TardanzaRowDto {

    private Long alumnoId;
    private String apellido;
    private String nombre;
    private String dni;

    private Long cursoId;
    private Integer cursoAnio;
    private Division cursoDivision;
    private long cantidadTardanzas;


}
