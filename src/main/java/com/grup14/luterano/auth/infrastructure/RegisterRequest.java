package com.grup14.luterano.auth.infrastructure;


import com.grup14.luterano.entities.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message = "El mail es obligatorio")
    @Email(message = "El mail es incorrecto")
    private String email;

    @NotBlank(message = "El nombre no puede ser null")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotBlank(message = "La contraseña es obligatoria")
    @Length(min = 5, message = "La contraseña debe tener como mínimo 5 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Rol role;

}
