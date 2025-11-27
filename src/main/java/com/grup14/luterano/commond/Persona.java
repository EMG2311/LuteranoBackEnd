package com.grup14.luterano.commond;

import com.grup14.luterano.entities.enums.TipoDoc;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
public class Persona {
    private String nombre;
    private String apellido;

    @Enumerated(EnumType.STRING)
    private GeneroEnum genero;

    @Enumerated(EnumType.STRING)
    private TipoDoc tipoDoc;

    private String dni;
    private String email;
    private String direccion;
    private String telefono;
    private Date fechaNacimiento;
    private Date fechaIngreso;

}
