package com.grup14.luterano.service;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.request.UserUpdateRequest;
import com.grup14.luterano.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> listUserFiltro(UserStatus userStatus);
    User updateUser(UserUpdateRequest userUpdate);
    User getUsuario();
    User crearUsuario(String mail);
    User borrarUsuario(String mail);
}
