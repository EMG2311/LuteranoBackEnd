package com.grup14.luterano.commond;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.entities.enums.TipoDoc;
import com.grup14.luterano.request.docente.DocenteRequest;
import com.grup14.luterano.validation.MayorDeEdad;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import com.grup14.luterano.validation.UpdateValidacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor@NoArgsConstructor
@Data
public class PersonaDto {
    @NotNull(groups = {UpdateValidacion.class})
    private Long id;
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotNull(message = "Debe seleccionar un género")
    private GeneroEnum genero;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDoc tipoDoc;

    @NotBlank(message = "El DNI no puede estar vacío")
    private String dni;

    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @MayorDeEdad(groups = {MayorDeEdadGruoup.class,UpdateValidacion.class})
    private Date fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private Date fechaIngreso;
}
