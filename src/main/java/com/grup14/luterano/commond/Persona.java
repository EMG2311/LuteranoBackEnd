package com.grup14.luterano.commond;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class Persona {
    private String nombre;
    private String apellido;
    private GeneroEnum genero;
    private String tipoDoc;
    private String dni;
    private String email;
    private String direccion;
    private String telefono;
    private Date fechaNacimiento;
    private Date fechaIngreso;

}
