package com.grup14.luterano.response;


import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.enums.UserStatus;
import lombok.*;

@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String email;
    private Role role;
    private UserStatus userStatus;
    private String mensaje;
    private Integer code;

}
