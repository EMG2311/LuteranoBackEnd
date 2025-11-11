package com.grup14.luterano.auth.application;

import com.grup14.luterano.auth.infrastructure.AuthenticateException;
import com.grup14.luterano.auth.infrastructure.AuthenticationRequest;
import com.grup14.luterano.auth.infrastructure.AuthenticationResponse;
import com.grup14.luterano.auth.infrastructure.RegisterRequest;
import com.grup14.luterano.dto.UserDto;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import com.grup14.luterano.event.UserEvent;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.user.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private PreceptorRepository preceptorRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private TutorRepository tutorRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest) {

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent())
            throw new AuthenticateException("El email ya se encuentra registrado");
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRol(roleRepository.findByName(registerRequest.getRole().name()).get());
        user.setUserStatus(UserStatus.CREADO);
        user.setName(registerRequest.getName());
        user.setLastName(registerRequest.getLastName());

        User saved = userRepository.save(user);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRol().getName());
        String jwtToken = jwtService.generateToken(extraClaims, user);

        applicationEventPublisher.publishEvent(
                new UserEvent(this, UserEvent.Tipo.CREAR, user, registerRequest.getPassword())
        );

        return AuthenticationResponse.builder()
                .token(null)
                .mensaje("El usuario se registro correctamente")
                .code(0)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticateException {
        try {
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
            extraClaims.put("userId", user.getId());
            
            // Agregar información específica según el rol
            String roleName = user.getRol().getName();
            switch (roleName) {
                case "ROLE_DOCENTE":
                    docenteRepository.findByEmail(user.getEmail())
                        .ifPresent(docente -> extraClaims.put("docenteId", docente.getId()));
                    break;
                case "ROLE_PRECEPTOR":
                    preceptorRepository.findByEmail(user.getEmail())
                        .ifPresent(preceptor -> extraClaims.put("preceptorId", preceptor.getId()));
                    break;
                case "ROLE_ALUMNO":
                    alumnoRepository.findByEmail(user.getEmail())
                        .ifPresent(alumno -> extraClaims.put("alumnoId", alumno.getId()));
                    break;
                case "ROLE_TUTOR":
                    tutorRepository.findByEmail(user.getEmail())
                        .ifPresent(tutor -> extraClaims.put("tutorId", tutor.getId()));
                    break;
                // Para ADMIN, DIRECTOR, AUXILIAR no hay entidades específicas
                case "ROLE_ADMIN":
                case "ROLE_DIRECTOR":
                case "ROLE_AUXILIAR":
                default:
                    // No agregamos IDs adicionales para estos roles
                    break;
            }
            
            String jwtToken = jwtService.generateToken(extraClaims, user);
            logger.info("----------Se loggeo " + user.getEmail() + " (" + roleName + ")----------");
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .mensaje("hola " + user.getName() + " " + user.getLastName())
                    .code(0)
                    .build();
        } catch (BadCredentialsException e) {
            logger.error(request.getEmail() + " ERROR: " + e.getMessage());
        }
        return null;
    }

}
