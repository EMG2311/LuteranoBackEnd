package com.grup14.luterano.dto.reporteTardanza;

import com.grup14.luterano.entities.enums.Division;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class TardanzaRowDto {

    private Long alumnoId;
    private String apellido;
    private String nombre;
    private String dni;

    private Long cursoId;
    private Integer cursoAnio;
    private Division cursoDivision;
    private long cantidadTardanzas;
    
    // Lista de detalles de cada tardanza con fecha y observación
    private List<TardanzaDetalleDto> detalles;

    // Constructor completo para uso manual
    public TardanzaRowDto(Long alumnoId, String apellido, String nombre, String dni,
                          Long cursoId, Integer cursoAnio, Division cursoDivision,
                          long cantidadTardanzas, List<TardanzaDetalleDto> detalles) {
        this.alumnoId = alumnoId;
        this.apellido = apellido;
        this.nombre = nombre;
        this.dni = dni;
        this.cursoId = cursoId;
        this.cursoAnio = cursoAnio;
        this.cursoDivision = cursoDivision;
        this.cantidadTardanzas = cantidadTardanzas;
        this.detalles = detalles;
    }

    // Constructor para JPQL (sin detalles, se llenan después)
    public TardanzaRowDto(Long alumnoId, String apellido, String nombre, String dni,
                          Long cursoId, Integer cursoAnio, Division cursoDivision,
                          long cantidadTardanzas) {
        this.alumnoId = alumnoId;
        this.apellido = apellido;
        this.nombre = nombre;
        this.dni = dni;
        this.cursoId = cursoId;
        this.cursoAnio = cursoAnio;
        this.cursoDivision = cursoDivision;
        this.cantidadTardanzas = cantidadTardanzas;
    }

}
