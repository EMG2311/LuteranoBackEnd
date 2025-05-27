package com.grup14.luterano.controller;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.response.UserResponse;
import com.grup14.luterano.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{status}")
    public ResponseEntity<List<UserResponse>> listarUsuariosFiltro(@PathVariable UserStatus status) {
        return ResponseEntity.ok(userService.listUserFiltro(status));
    }


}
