package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.UserUpdateRequest;
import com.grup14.luterano.response.UserResponse;
import com.grup14.luterano.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public List<UserResponse> listUserFiltro(UserStatus userStatus) {
        List<UserResponse> userResponses = new ArrayList<>();
        for(User user : userRepository.findByUserStatus(userStatus).get()){
            userResponses.add(UserResponse.builder()
                            .email(user.getEmail())
                            .role(user.getRol())
                            .userStatus(user.getUserStatus())
                    .build());
        }
        return userResponses;
    }


    @Override
    public User updateUser(UserUpdateRequest userUpdate) {
        return null;
    }

    @Override
    public User getUsuario() {
        return null;
    }

    @Override
    public User crearUsuario(String mail) {
        return null;
    }

    @Override
    public User borrarUsuario(String mail) {
        return null;
    }
}
