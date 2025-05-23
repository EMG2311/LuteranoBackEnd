package com.grup14.luterano.auth.infrastructure;


import jakarta.validation.constraints.Email;
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
    @NotNull(message = "La contraseña es obligatoria")
    @Length(min = 5, message = "La contraseña debe tener como minimo 5 caracteres")
    private String password;

}
