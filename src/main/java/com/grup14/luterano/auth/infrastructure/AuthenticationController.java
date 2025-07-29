package com.grup14.luterano.auth.infrastructure;

import com.grup14.luterano.auth.application.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    @Operation(summary = "Loggea al usuario", description = "Devuelve el token con el que se acceden a los demas endpoint")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        try{
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
            if(authenticationResponse==null){
                return ResponseEntity.status(422).body(AuthenticationResponse.builder()
                        .token("")
                        .code(-1)
                        .mensaje("Credenciales incorrectas").build());
            }
            return ResponseEntity.ok(authenticationResponse);
        }catch (AuthenticateException e){
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .token(null)
                    .mensaje(e.getMessage())
                    .code(-1)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registra un usuario",description = "Registra al usuario con rol DE_VISITA, devuelve el token")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Validated RegisterRequest registerRequest) throws AuthenticateException {
        try {
            AuthenticationResponse response = authenticationService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (AuthenticateException e) {
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .mensaje(e.getMessage())
                    .code(-1)
                    .build();
            return ResponseEntity.status(422).body(errorResponse);
        }
    }


}
