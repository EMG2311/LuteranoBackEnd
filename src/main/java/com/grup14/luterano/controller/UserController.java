package com.grup14.luterano.controller;

import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.exeptions.UserException;
import com.grup14.luterano.request.EmailRequest;
import com.grup14.luterano.request.UserUpdateRequest;
import com.grup14.luterano.response.user.UserCreadoResponse;
import com.grup14.luterano.response.user.UserResponse;
import com.grup14.luterano.response.user.UserUpdateResponse;
import com.grup14.luterano.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Lista todos los usuarios", description = "Lista todos los usuarios, solo ADMIN y DIRECTOR pueden usar")
    public ResponseEntity<List<UserResponse>> listarTodosUsuarios() {
        return ResponseEntity.ok(userService.listAllUser());
    }

    @GetMapping("/{status}")
    @Operation(summary = "Lista usuarios según filtro", description = "Lista usuarios según el filtro, solo ADMIN y DIRECTOR pueden usar")
    public ResponseEntity<List<UserResponse>> listarUsuariosFiltro(@PathVariable@Valid UserStatus status) {
        return ResponseEntity.ok(userService.listUserFiltro(status));
    }

    @PostMapping("/activar")
    @Operation(summary = "Completar creacion Usuarios", description = "Cambia el status del usuario a CREADO")
    public ResponseEntity<UserCreadoResponse> ActivarCuenta(@RequestBody @Valid EmailRequest email){
        try{
            return ResponseEntity.ok(userService.ActivarCuenta(email));
        }catch (UserException e){
            return ResponseEntity.status(422).body(UserCreadoResponse.builder()
                            .email(null)
                            .code(-1)
                            .mensaje(e.getMessage())
                            .role(null)
                            .userStatus(null)
                    .build());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza los usuarios", description = "Actualiza el usuario, se pasa el id, los demas campos se completan por el valor a actualizar")
    public ResponseEntity<UserUpdateResponse> updateUser(@Valid@RequestBody  UserUpdateRequest userUpdateRequest){
        try{
            return ResponseEntity.ok(userService.updateUser(userUpdateRequest));
        }catch (UserException u){
            return ResponseEntity.status(422).body(UserUpdateResponse.builder()
                    .email(userUpdateRequest.getEmail())
                    .rol(userUpdateRequest.getRol())
                    .mensaje(u.getMessage())
                    .code(-1)
                    .build());
        }
    }
    @GetMapping("/email")
    @Operation(summary = "Busca un usuario con mail")
    public ResponseEntity<UserResponse> getUsuarioByEmail(@Valid@RequestBody EmailRequest emailRequest){
        try{
            return ResponseEntity.ok(userService.getUsuarioByEmail(emailRequest.getEmail()));
        }catch (EntityNotFoundException u){
            return ResponseEntity.status(422).body(UserResponse.builder()
                    .email(emailRequest.getEmail())
                    .code(-1)
                    .mensaje("No se encontro el usuario")
                    .build());
        }
    }

    @DeleteMapping("/email")
    @Operation(summary = "Elimina un usuario con mail")
    public ResponseEntity<UserResponse> borrarUser(@Valid@RequestBody EmailRequest emailRequest){
        try{
            return ResponseEntity.ok(userService.borrarUsuario(emailRequest.getEmail()));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(422).body(UserResponse.builder().email(emailRequest.getEmail())
                    .mensaje("No existe ese mail")
                    .code(-1)
                    .build());
        }
    }







}
