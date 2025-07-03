package com.grup14.luterano.response.user;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserUpdateResponse {
    @NotNull(message = "El mail es un campo obligatorio")
    private String email;
    private Role rol;
    private UserStatus userStatus;
    private String mensaje;
    private Integer code;
}
