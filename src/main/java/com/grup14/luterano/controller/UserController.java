package com.grup14.luterano.controller;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.exeptions.UserException;
import com.grup14.luterano.request.EmailRequest;
import com.grup14.luterano.request.user.UserUpdateRequest;
import com.grup14.luterano.response.user.UserCreadoResponse;
import com.grup14.luterano.response.user.UserListResponse;
import com.grup14.luterano.response.user.UserResponse;
import com.grup14.luterano.response.user.UserUpdateResponse;
import com.grup14.luterano.service.EmailServiceImpl;
import com.grup14.luterano.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@CrossOrigin(origins = "*")
@Tag(
        name = "User Controller",
        description = "Controlador encargado de la gestión los usuarios. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

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
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserCreadoResponse.builder()
                            .code(-2)
                            .mensaje("Error no contorlado: "+e.getMessage())
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
                    .rol(null)
                    .mensaje(u.getMessage())
                    .code(-1)
                    .build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserUpdateResponse.builder()
                    .code(-2)
                    .mensaje("Error no contorlado: "+e.getMessage())
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
        }catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(UserResponse.builder()
                    .code(-3)
                    .mensaje("No se puede eliminar el usuario que esta asignado.")
                    .build());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponse.builder()
                    .code(-2)
                    .mensaje("Error no contorlado: "+e.getMessage())
                    .build());
        }
    }



    @GetMapping("/sin-asignar")
    @Operation(summary = "Listar usuarios sin asignar", description = "Devuelve todos los usuarios que no están asignados a Docente, Preceptor u otro rol específico")
    public ResponseEntity<UserListResponse> listUsuariosSinAsignar() {
        try {
            return ResponseEntity.ok(userService.listUserSinAsignar());
        } catch (UserException e) {
            return ResponseEntity.status(422).body(UserListResponse.builder()
                    .usuarios(null)
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserListResponse.builder()
                    .code(-2)
                    .mensaje("Error no contorlado: "+e.getMessage())
                    .build());
        }
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Listar usuarios por rol", description = "Devuelve usuarios filtrados por el rol indicado")
    public ResponseEntity<UserListResponse> listUserPorRol(@PathVariable Rol rol) {
        try {
            UserListResponse response = userService.listUserRol(rol);
            return ResponseEntity.ok(response);
        } catch (UserException e) {
            return ResponseEntity.status(422).body(UserListResponse.builder()
                    .usuarios(null)
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/sin-asignar/rol/{rol}")
    @Operation(summary = "Listar usuarios sin asignar por rol", description = "Devuelve usuarios con el rol indicado que no están asignados a Docente, Preceptor u otro")
    public ResponseEntity<UserListResponse> listUserSinAsignarPorRol(@PathVariable Rol rol) {
        try {
            UserListResponse response = userService.listUserSinAsignarPorRol(rol);
            return ResponseEntity.ok(response);
        } catch (UserException e) {
            return ResponseEntity.status(422).body(UserListResponse.builder()
                    .usuarios(null)
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(UserListResponse.builder()
                    .usuarios(null)
                    .code(-2)
                    .mensaje("Error no controlado: " + e.getMessage())
                    .build());
        }

    }


}
