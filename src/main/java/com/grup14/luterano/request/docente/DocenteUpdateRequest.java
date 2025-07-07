package com.grup14.luterano.request.docente;

import com.grup14.luterano.commond.GeneroEnum;
import com.grup14.luterano.commond.Persona;
import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.entities.enums.TipoDoc;
import com.grup14.luterano.validation.MayorDeEdad;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Builder@Data@NoArgsConstructor@AllArgsConstructor
public class DocenteUpdateRequest {
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
    @MayorDeEdad(groups = {MayorDeEdadGruoup.class})
    private Date fechaNacimiento;
    private Date fechaIngreso;
}
