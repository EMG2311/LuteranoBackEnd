package com.grup14.luterano.response.user;

import com.grup14.luterano.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder@Data@AllArgsConstructor@NoArgsConstructor
public class UserListResponse {
    List<UserDto> usuarios;
    Integer code;
    String mensaje;
}
