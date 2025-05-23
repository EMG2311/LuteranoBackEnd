package com.grup14.luterano.auth.application;

import com.grup14.luterano.auth.infrastructure.AuthenticateException;
import com.grup14.luterano.auth.infrastructure.AuthenticationRequest;
import com.grup14.luterano.auth.infrastructure.RegisterRequest;
import com.grup14.luterano.auth.infrastructure.AuthenticationResponse;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthenticationResponse register(RegisterRequest registerRequest) throws AuthenticateException {

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) throw new AuthenticateException("El email ya se encuentra registrado");
        User user=new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRol(Rol.ALUMNO);

        User saved = userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .mensaje("El usuario se registro correctamente")
                .code(0)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticateException {
    try{
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
    }catch (BadCredentialsException e){
        System.out.printf(request.getEmail() + " ERROR: " + e.getMessage());
    }


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticateException("Usuario no encontrado"));


        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .mensaje("hola "+user.getEmail())
                .code(0)
                .build();
    }
}
