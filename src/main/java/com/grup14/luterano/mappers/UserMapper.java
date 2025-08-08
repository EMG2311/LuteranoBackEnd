package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.UserDto;
import com.grup14.luterano.entities.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .rol(user.getRol())
                .build();
    }

}
