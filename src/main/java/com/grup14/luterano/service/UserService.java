package com.grup14.luterano.service;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.request.EmailRequest;
import com.grup14.luterano.request.UserUpdateRequest;
import com.grup14.luterano.response.UserCreadoResponse;
import com.grup14.luterano.response.UserResponse;
import com.grup14.luterano.response.UserUpdateResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> listUserFiltro(UserStatus userStatus);
    List<UserResponse> listAllUser();
    UserCreadoResponse ActivarCuenta(EmailRequest email);
    UserUpdateResponse updateUser(UserUpdateRequest userUpdate);
    UserResponse getUsuarioByEmail(String email);
    UserResponse borrarUsuario(String mail);
}
