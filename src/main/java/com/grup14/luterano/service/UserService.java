package com.grup14.luterano.service;

import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.request.EmailRequest;
import com.grup14.luterano.request.user.UserUpdateRequest;
import com.grup14.luterano.response.user.UserCreadoResponse;
import com.grup14.luterano.response.user.UserListResponse;
import com.grup14.luterano.response.user.UserResponse;
import com.grup14.luterano.response.user.UserUpdateResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> listUserFiltro(UserStatus userStatus);
    List<UserResponse> listAllUser();
    UserCreadoResponse ActivarCuenta(EmailRequest email);
    UserUpdateResponse updateUser(UserUpdateRequest userUpdate);
    UserResponse getUsuarioByEmail(String email);
    UserResponse borrarUsuario(String mail);
    UserListResponse listUserRol(Rol rol);
    UserListResponse listUserSinAsignar();
    UserListResponse listUserSinAsignarPorRol(Rol rol);
}
