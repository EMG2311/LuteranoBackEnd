package com.grup14.luterano.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor
public class EmailRequest {
    @NotBlank(message = "El mail no puede estar vacio")
    private String email;
}
