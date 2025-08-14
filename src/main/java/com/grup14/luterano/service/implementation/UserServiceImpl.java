package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.UserDto;
import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.event.UserEvent;
import com.grup14.luterano.exeptions.UserException;
import com.grup14.luterano.mappers.UserMapper;
import com.grup14.luterano.repository.RoleRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.EmailRequest;
import com.grup14.luterano.request.user.UserUpdateRequest;
import com.grup14.luterano.response.user.UserCreadoResponse;
import com.grup14.luterano.response.user.UserListResponse;
import com.grup14.luterano.response.user.UserResponse;
import com.grup14.luterano.response.user.UserUpdateResponse;
import com.grup14.luterano.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
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
        if(userUpdate.getName() != null){
            user.setName(userUpdate.getName());
        }
        if(userUpdate.getLastName() != null){
            user.setLastName(userUpdate.getLastName());
        }
        if (userUpdate.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }
        if (userUpdate.getUserStatus() != null) {
            user.setUserStatus(userUpdate.getUserStatus());
        }
        if (userUpdate.getRol() != null && !user.getRol().getName().equals(userUpdate.getRol().name())) {

            Role rol = roleRepository.findByName(userUpdate.getRol().name())
                    .orElseThrow(() -> new UserException("El rol no existe"));

            UserListResponse userListResponse = this.listUserSinAsignar();
            List<UserDto> usuariosPermitidos = userListResponse.getUsuarios();
            boolean permitido = usuariosPermitidos.stream()
                    .anyMatch(u -> u.getId().equals(userUpdate.getId()));
            if (!permitido) {
                throw new UserException("El usuario ya está asignado a otra entidad que no acepta este rol");
            }
            user.setRol(rol);
        }

        userRepository.save(user);
        
        eventPublisher.publishEvent(new UserEvent(this,UserEvent.Tipo.ACTUALIZAR,user));

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

    @Override
    public UserListResponse listUserRol(Rol rol) {
        Role role = roleRepository.findByName(rol.name())
                .orElseThrow(() -> new UserException("Rol no encontrado"));
        List<User> users = userRepository.findByRol(role).get();
        return UserListResponse.builder()
                .usuarios(users.stream().map(UserMapper::toDto).collect(Collectors.toList()))
                .code(0)
                .mensaje("Se listaron correctamente")
                .build();
    }

    @Override
    public UserListResponse listUserSinAsignar() {
        List<User> users= userRepository.findUsuariosSinAsignar().get();
        return UserListResponse.builder()
                .usuarios(users.stream().map(UserMapper::toDto).collect(Collectors.toList()))
                .code(0)
                .mensaje("Se listaron correctamente")
                .build();
    }

    @Override
    public UserListResponse listUserSinAsignarPorRol(Rol rol) {
        Role role = roleRepository.findByName(rol.name())
                .orElseThrow(() -> new UserException("Rol no encontrado"));

        List<User> users = userRepository.findUsuariosSinAsignarPorRol(role).get();

        return UserListResponse.builder()
                .usuarios(users.stream().map(UserMapper::toDto).collect(Collectors.toList()))
                .code(0)
                .mensaje("Se listaron correctamente los usuarios sin asignar con rol " + rol.name())
                .build();
    }
}
