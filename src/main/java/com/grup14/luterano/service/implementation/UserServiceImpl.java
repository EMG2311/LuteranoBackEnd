package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.exeptions.UserException;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.EmailRequest;
import com.grup14.luterano.request.user.UserUpdateRequest;
import com.grup14.luterano.response.user.UserCreadoResponse;
import com.grup14.luterano.response.user.UserResponse;
import com.grup14.luterano.response.user.UserUpdateResponse;
import com.grup14.luterano.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public List<UserResponse> listUserFiltro(UserStatus userStatus) {
        List<UserResponse> userResponses = new ArrayList<>();

        for(User user : userRepository.findByUserStatus(userStatus).get()){
            userResponses.add(UserResponse.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .name(user.getName())
                            .lastName(user.getLastName())
                            .role(user.getRol())
                            .userStatus(user.getUserStatus())
                            .mensaje("")
                            .code(0)
                    .build());
        }
        return userResponses;
    }
    public List<UserResponse> listAllUser() {
        List<UserResponse> userResponses = new ArrayList<>();

        for(User user : userRepository.findAll()){
            userResponses.add(UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .lastName(user.getLastName())
                    .role(user.getRol())
                    .userStatus(user.getUserStatus())
                    .mensaje("")
                    .code(0)
                    .build());
        }
        return userResponses;
    }

    @Override
    @Transactional
    public UserCreadoResponse ActivarCuenta(EmailRequest email) {
        User user = userRepository.findByEmail(email.getEmail())
                .orElseThrow(() -> new UserException("Usuario " + email.getEmail() + " no encontrado"));
        if (user.getUserStatus() != UserStatus.CREADO){
            logger.error("--------- El usuario " + email.getEmail()+" tiene el estado no esta borrado-------");
            throw new UserException("El usuario " + email.getEmail()+ " tiene el estado no esta borrado");
        }

        user.setUserStatus(UserStatus.CREADO);
        userRepository.save(user);
        logger.info("---------- Se completo la creacion del usuario "+ email.getEmail()+" ----------");
        return UserCreadoResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .role(user.getRol())
                .code(0)
                .mensaje("Se completo la creacion del usuario con exito")
                .userStatus(user.getUserStatus())
                .build();
    }


    @Override
    @Transactional
    public UserUpdateResponse updateUser(UserUpdateRequest userUpdate) {
        User user = userRepository.findById(userUpdate.getId())
                .orElseThrow(() -> new UserException("No existe el id "+userUpdate.getId()));

        if (userUpdate.getEmail() != null) {
            user.setEmail(userUpdate.getEmail());
        }
        if (userUpdate.getPassword() != null) {
            user.setPassword(userUpdate.getPassword());
        }
        if (userUpdate.getUserStatus() != null) {
            user.setUserStatus(userUpdate.getUserStatus());
        }
        if (userUpdate.getRol() != null) {
            user.setRol(userUpdate.getRol());
        }

        userRepository.save(user);

        return UserUpdateResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .rol(user.getRol())
                .userStatus(user.getUserStatus())
                .mensaje("Se actualizó correctamente el usuario")
                .code(0)
                .build();
    }

    @Override
    public UserResponse getUsuarioByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        return UserResponse.builder().email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .userStatus(user.getUserStatus())
                .role(user.getRol())
                .build();
    }


    @Override
    @Transactional
    public UserResponse borrarUsuario(String email) {
        User user = userRepository.findByEmail(email).get();
        if(user.getUserStatus()==UserStatus.BORRADO){
            throw new UserException("El usuario "+email+" ya esta borrado");
        }
        user.setUserStatus(UserStatus.BORRADO);
        userRepository.delete(user); //No hago borrado virtual
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .role(user.getRol())
                .userStatus(UserStatus.BORRADO)
                .build();
    }
}
