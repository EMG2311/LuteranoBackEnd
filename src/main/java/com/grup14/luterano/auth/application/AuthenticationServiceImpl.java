package com.grup14.luterano.auth.application;

import com.grup14.luterano.auth.infrastructure.AuthenticateException;
import com.grup14.luterano.auth.infrastructure.AuthenticationRequest;
import com.grup14.luterano.auth.infrastructure.RegisterRequest;
import com.grup14.luterano.auth.infrastructure.AuthenticationResponse;
import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.repository.RoleRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.service.implementation.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest)  {

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) throw new AuthenticateException("El email ya se encuentra registrado");
        User user=new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRol(roleRepository.findByName(registerRequest.getRole().name()).get());
        user.setUserStatus(UserStatus.CREADO);
        user.setName(registerRequest.getName());
        user.setLastName(registerRequest.getLastName());

        User saved = userRepository.save(user);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRol().getName());
        String jwtToken = jwtService.generateToken(extraClaims,user);

        return AuthenticationResponse.builder()
                .token(null)
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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticateException("Usuario no encontrado"));
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRol().getName());
        String jwtToken = jwtService.generateToken(extraClaims,user);
        logger.info("----------Se loggeo " + user.getEmail() + "----------");
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .mensaje("hola "+user.getName() + " "+user.getLastName())
                .code(0)
                .build();
    }catch (BadCredentialsException e){
        logger.error(request.getEmail() + " ERROR: " + e.getMessage());
    }
    return null;
    }

}
