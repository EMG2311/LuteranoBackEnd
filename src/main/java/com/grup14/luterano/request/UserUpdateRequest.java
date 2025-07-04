package com.grup14.luterano.request;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@Builder
public class UserUpdateRequest {
    @NotNull(message = "El mail es un campo obligatorio")
    private String email;
    private String emailNuevo;
    private String password;
    private Role rol;
    private UserStatus userStatus;
}
