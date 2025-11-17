package com.grup14.luterano.commond;

import com.grup14.luterano.entities.enums.TipoDoc;
import com.grup14.luterano.validation.MayorDeEdad;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonaDto {
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
    @Pattern(
            regexp = "^[0-9]{7,8}$",
            message = "El DNI debe tener 7 u 8 dígitos numéricos"
    )
    private String dni;

    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^[0-9]{6,15}$",
            message = "El teléfono debe contener solo números y tener entre 6 y 15 dígitos"
    )
    private String telefono;
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @MayorDeEdad(groups = {MayorDeEdadGruoup.class})
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private Date fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @Past(message = "La fecha de ingreso debe ser anterior a la fecha actual")
    private Date fechaIngreso;
}
