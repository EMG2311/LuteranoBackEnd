package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.exeptions.UserException;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.EmailRequest;
import com.grup14.luterano.request.UserUpdateRequest;
import com.grup14.luterano.response.UserCreadoResponse;
import com.grup14.luterano.response.UserResponse;
import com.grup14.luterano.response.UserUpdateResponse;
import com.grup14.luterano.service.UserService;
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
                            .email(user.getEmail())
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
                    .email(user.getEmail())
                    .role(user.getRol())
                    .userStatus(user.getUserStatus())
                    .mensaje("")
                    .code(0)
                    .build());
        }
        return userResponses;
    }

    @Override
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
                .role(user.getRol())
                .code(0)
                .mensaje("Se completo la creacion del usuario con exito")
                .userStatus(user.getUserStatus())
                .build();
    }


    @Override
    public UserUpdateResponse updateUser(UserUpdateRequest userUpdate) {
        User user = userRepository.findByEmail(userUpdate.getEmail()).get();
        user.setEmail(userUpdate.getEmailNuevo()!=null?userUpdate.getEmailNuevo() : user.getEmail());
        user.setPassword(userUpdate.getPassword() != null ? userUpdate.getPassword() : user.getPassword());
        user.setUserStatus(userUpdate.getUserStatus() != null ? userUpdate.getUserStatus() : user.getUserStatus());
        user.setRol(userUpdate.getRol() != null ? userUpdate.getRol() : user.getRol());
        userRepository.save(user);
        return UserUpdateResponse.builder()
                .email(userUpdate.getEmail())
                .rol(userUpdate.getRol())
                .userStatus(userUpdate.getUserStatus())
                .mensaje("Se actualizo correctamente el usuario")
                .code(0)
                .build();
    }

    @Override
    public UserResponse getUsuarioByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        return UserResponse.builder().email(user.getEmail())
                .userStatus(user.getUserStatus())
                .role(user.getRol())
                .build();
    }


    @Override
    public UserResponse borrarUsuario(String email) {
        User user = userRepository.findByEmail(email).get();
        if(user.getUserStatus()==UserStatus.BORRADO){
            logger.error("El usuario "+email+" esta borrado");
            throw new UserException("El usuario "+email+" ya esta borrado");
        }
        user.setUserStatus(UserStatus.BORRADO);
        userRepository.delete(user); //No hago borrado virtual
        return UserResponse.builder()
                .email(user.getEmail())
                .role(user.getRol())
                .userStatus(UserStatus.BORRADO)
                .build();
    }
}
