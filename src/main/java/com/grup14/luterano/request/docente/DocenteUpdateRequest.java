package com.grup14.luterano.request.docente;

import com.grup14.luterano.commond.GeneroEnum;
import com.grup14.luterano.entities.enums.TipoDoc;
import com.grup14.luterano.validation.MayorDeEdad;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocenteUpdateRequest {
    @NotNull(message = "El ID es obligatorio")
    private Long id;
    private String nombre;
    private String apellido;
    private GeneroEnum genero;
    private TipoDoc tipoDoc;
    @Pattern(
            regexp = "^[0-9]{7,8}$",
            message = "El DNI debe tener 7 u 8 dígitos numéricos"
    )
    private String dni;
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;
    private String direccion;
    @Pattern(
            regexp = "^[0-9]{6,15}$",
            message = "El teléfono debe contener solo números y tener entre 6 y 15 dígitos"
    )
    private String telefono;
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    @MayorDeEdad(groups = {MayorDeEdadGruoup.class})
    private Date fechaNacimiento;
    @Past(message = "La fecha de ingreso debe ser anterior a la fecha actual")
    private Date fechaIngreso;

}
