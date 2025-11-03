package com.grup14.luterano.request.alumno;

import com.grup14.luterano.commond.GeneroEnum;
import com.grup14.luterano.entities.enums.TipoDoc;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoUpdateRequest {
    // para actualizar un alumno ante una solicitud HTTP (PUT),recibe los DATOS ENTRADA del alumno que el cliente envía
    @NotNull(message = "El ID es obligatorio")
    private Long id;
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
