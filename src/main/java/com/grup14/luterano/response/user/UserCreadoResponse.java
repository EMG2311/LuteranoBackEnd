package com.grup14.luterano.response.user;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.enums.UserStatus;
import lombok.*;

@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserCreadoResponse {
    private String email;
    private String name;
    private String lastName;
    private Role role;
    private UserStatus userStatus;
    private String mensaje;
    private Integer code;
}
