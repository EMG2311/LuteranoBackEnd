package com.grup14.luterano.commond;

import com.grup14.luterano.entities.enums.TipoDoc;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@MappedSuperclass@SuperBuilder@Data
public class Persona {
    private String nombre;
    private String apellido;
    private GeneroEnum genero;
    private TipoDoc tipoDoc;
    private String dni;
    private String email;
    private String direccion;
    private String telefono;
    private Date fechaNacimiento;
    private Date fechaIngreso;

}
