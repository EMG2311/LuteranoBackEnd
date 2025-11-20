package com.grup14.luterano.dto.mesaExamen;

import com.grup14.luterano.entities.enums.EstadoMateriaAlumno;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlumnoDebeMateriaDto {
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;
    private String curso;          // Ej: "4° A"
    private Integer anioCiclo;     // Ej: 2025
    private EstadoMateriaAlumno estado; // normalmente DESAPROBADA
}
