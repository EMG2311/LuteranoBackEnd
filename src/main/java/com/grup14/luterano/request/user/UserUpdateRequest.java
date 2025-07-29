package com.grup14.luterano.request.user;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@Builder
public class UserUpdateRequest {
    @NotNull(message = "El Id es un campo obligatorio")
    private Long id;
    private String email;
    private String password;
    private Role rol;
    private UserStatus userStatus;
}
