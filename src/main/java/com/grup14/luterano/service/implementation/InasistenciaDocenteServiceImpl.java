package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.AulaDto;
import com.grup14.luterano.dto.docente.InasistenciaDocenteDto;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.InasistenciaDocente;
import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.exeptions.InasistenciaDocenteException;
import com.grup14.luterano.mappers.InasistenciaDocenteMapper;
import com.grup14.luterano.repository.DocenteRepository;
import com.grup14.luterano.repository.InasistenciaDocenteRepository;
import com.grup14.luterano.repository.PreceptorRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.docente.InasistenciaDocenteRequest;
import com.grup14.luterano.request.docente.InasistenciaDocenteUpdateRequest;
import com.grup14.luterano.response.docente.InasistenciaDocenteResponse;
import com.grup14.luterano.response.docente.InasistenciaDocenteResponseList;
import com.grup14.luterano.service.InasistenciaDocenteService;
import com.grup14.luterano.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class InasistenciaDocenteServiceImpl implements InasistenciaDocenteService {

    @Autowired
    private InasistenciaDocenteRepository inasistenciaDocenteRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private PreceptorRepository preceptorRepository;

    @Autowired
    private UserService UserService;
    @Autowired
    private UserRepository UserRepository;
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AulaServiceImpl.class);



    @Override
    @Transactional
    public InasistenciaDocenteResponse crearInasistenciaDocente(InasistenciaDocenteRequest inasistenciaDocenteRequest) {

        // 1. Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }

        User usuario = optionalUser.get();


        //  Validar que el docente exista
        Optional<Docente> docenteOptional = docenteRepository.findById(inasistenciaDocenteRequest.getDocenteId());
        if (docenteOptional.isEmpty()) {
            throw new IllegalArgumentException("El docente con el ID proporcionado no existe.");
        }
        Docente docente = docenteOptional.get();


        //   Construcción de la entidad InasistenciaDocente
        InasistenciaDocente inasistencia = InasistenciaDocente.builder()
                .fecha(LocalDate.now())
                .estado(inasistenciaDocenteRequest.getEstado())
                .docente(docente)
                .usuario(usuario) // Asigna el usuario capturado de la sesión
                .build();

        // Guardar la entidad
        InasistenciaDocente savedInasistencia = inasistenciaDocenteRepository.save(inasistencia);

        // Usar el mapper solo para convertir la entidad a DTO para la respuesta
        InasistenciaDocenteDto savedDto = InasistenciaDocenteMapper.toDto(savedInasistencia);

        // Construir y devolver la respuesta
        return InasistenciaDocenteResponse.builder()
               .InasistenciaDocente(savedDto)
                .code(201)
                .mensaje("Inasistencia registrada exitosamente.")
                .build();
    }

    @Override
    @Transactional
    public InasistenciaDocenteResponse updateInasistenciaDocente(Long id,InasistenciaDocenteUpdateRequest request) {
        //  Validar el ID
        if (id == null) {
            throw new IllegalArgumentException("El ID de la inasistencia no puede ser nulo.");
        }

        //  Buscar la inasistencia en la base de datos
        Optional<InasistenciaDocente> inasistenciaOptional = inasistenciaDocenteRepository.findById(id);

        //  Lanzar una excepción si la inasistencia no existe
        if (inasistenciaOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró la inasistencia con el ID proporcionado: " + id);
        }

        //  Actualizar el estado de la entidad
        InasistenciaDocente inasistencia = inasistenciaOptional.get();
        inasistencia.setEstado(request.getEstado());

        //  Guardar la entidad actualizada en la base de datos
        InasistenciaDocente updatedInasistencia = inasistenciaDocenteRepository.save(inasistencia);

        //  Construir y devolver la respuesta
        return InasistenciaDocenteResponse.builder()
                .InasistenciaDocente(InasistenciaDocenteMapper.toDto(updatedInasistencia))
                .code(200)
                .mensaje("Inasistencia actualizada exitosamente.")
                .build();
    }


    @Override
    public InasistenciaDocenteResponse deleteInasistenciaDocente(Long id) {

        InasistenciaDocente inasistencia = inasistenciaDocenteRepository.findById(id)
                .orElseThrow(() -> new InasistenciaDocenteException("No se encontró la inasistencia con ID: " + id));

        inasistenciaDocenteRepository.deleteById(id);
        logger.info ("Inasistencia docente eliminada con ID: {}", id);
        return InasistenciaDocenteResponse.builder()
                .InasistenciaDocente(InasistenciaDocenteMapper.toDto(inasistencia))
                .code(200)
                .mensaje("Inasistencia eliminada exitosamente.")
                .build();

    }

    @Override
    public InasistenciaDocenteResponse getInasistenciaDocenteById(Long id) {

        InasistenciaDocente inasistencia = inasistenciaDocenteRepository.findById(id)
                .orElseThrow(() -> new InasistenciaDocenteException("No se encontró la inasistencia con ID: " + id));

        return InasistenciaDocenteResponse.builder()
                .InasistenciaDocente(InasistenciaDocenteMapper.toDto(inasistencia))
                .code(200)
                .mensaje("Inasistencia encontrada exitosamente.")
                .build();

    }

    @Override
    public InasistenciaDocenteResponseList listInasistenciasDocente() {
        List<InasistenciaDocenteDto> inasistencias = inasistenciaDocenteRepository.findAll().stream()
                .map(InasistenciaDocenteMapper::toDto)
                .toList();
        return InasistenciaDocenteResponseList.builder()
                .inasistenciaDocenteDtos(inasistencias)
                .code(200)
                .mensaje("Lista de inasistencias obtenida exitosamente.")
                .build();

    }
}
