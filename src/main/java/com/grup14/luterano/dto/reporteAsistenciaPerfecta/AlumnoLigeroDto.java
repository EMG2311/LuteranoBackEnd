package com.grup14.luterano.dto.reporteAsistenciaPerfecta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoLigeroDto {
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;
}
