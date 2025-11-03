package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.PreceptorDto;
import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.exeptions.PreceptorException;
import com.grup14.luterano.mappers.PreceptorMapper;
import com.grup14.luterano.repository.PreceptorRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.Preceptor.PreceptorRequest;
import com.grup14.luterano.request.Preceptor.PreceptorUpdateRequest;
import com.grup14.luterano.response.Preceptor.PreceptorResponse;
import com.grup14.luterano.response.Preceptor.PreceptorResponseList;
import com.grup14.luterano.service.PreceptorService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PreceptorServiceImpl implements PreceptorService {
    private final PreceptorRepository preceptorRepository;
    private static final Logger logger = LoggerFactory.getLogger(PreceptorServiceImpl.class);
    private final UserRepository userRepository;

    public PreceptorServiceImpl(PreceptorRepository preceptorRepository,
                                UserRepository userRepository) {
        this.preceptorRepository = preceptorRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PreceptorResponse crearPreceptor(PreceptorRequest request) {
        preceptorRepository.findByEmailAndActiveIsTrue(request.getEmail())
                .ifPresent(p -> {
                    throw new PreceptorException("Ya existe un preceptor activo con ese email");
                });
        preceptorRepository.findByDniAndActiveIsTrue(request.getDni())
                .ifPresent(p -> {
                    throw new PreceptorException("Ya existe un preceptor activo con ese DNI");
                });

        Optional<Preceptor> preceptorInactivo = preceptorRepository.findByEmailAndActiveIsFalse(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new PreceptorException("No existe un usuario con ese mail. Por favor crearlo y volver a intentar"));

        if (!Rol.ROLE_PRECEPTOR.name().equals(user.getRol().getName())) {
            throw new PreceptorException("El usuario no tiene rol preceptor");
        }

        if (!Objects.equals(user.getName(), request.getNombre())
                || !Objects.equals(user.getLastName(), request.getApellido())) {
            user.setName(request.getNombre());
            user.setLastName(request.getApellido());
        }

        validarFechas(request.getFechaNacimiento(), request.getFechaIngreso());

        Preceptor preceptor;
        if (preceptorInactivo.isPresent()) {
            preceptor = preceptorInactivo.get();

            if (!Objects.equals(preceptor.getDni(), request.getDni())) {
                throw new PreceptorException(
                        "Existe un preceptor inactivo con ese email pero con distinto DNI. No se puede reactivar. " +
                                "El DNI registrado es: " + preceptor.getDni()
                );
            }

            preceptor.setActive(true);
            preceptor.setNombre(request.getNombre());
            preceptor.setApellido(request.getApellido());
            preceptor.setGenero(request.getGenero());
            preceptor.setTipoDoc(request.getTipoDoc());
            preceptor.setDireccion(request.getDireccion());
            preceptor.setTelefono(request.getTelefono());
            preceptor.setFechaNacimiento(request.getFechaNacimiento());
            preceptor.setFechaIngreso(request.getFechaIngreso());
            preceptor.setUser(user);
        } else {
            preceptor = Preceptor.builder()
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .genero(request.getGenero())
                    .tipoDoc(request.getTipoDoc())
                    .dni(request.getDni())
                    .email(request.getEmail())
                    .direccion(request.getDireccion())
                    .telefono(request.getTelefono())
                    .fechaNacimiento(request.getFechaNacimiento())
                    .fechaIngreso(request.getFechaIngreso())
                    .user(user)
                    .active(true)
                    .build();
        }

        preceptorRepository.save(preceptor);
        logger.info("Preceptor {} {} registrado/reactivado correctamente (id={})",
                preceptor.getNombre(), preceptor.getApellido(), preceptor.getId());

        return PreceptorResponse.builder()
                .preceptor(PreceptorMapper.toDto(preceptor))
                .code(0)
                .mensaje("Se registró/reactivó correctamente el preceptor")
                .build();
    }

    @Override
    public PreceptorResponse updatePreceptor(PreceptorUpdateRequest updateRequest) {
        Preceptor preceptor = preceptorRepository.findByIdAndActiveIsTrue(updateRequest.getId())
                .orElseThrow(() -> new PreceptorException("No existe preceptor activo con id: " + updateRequest.getId()));
        User user = preceptor.getUser();
        boolean necesitaActualizarUsuario = false;
        if (user == null) {
            throw new PreceptorException("El preceptor no tiene usuario asociado");
        }

        if (updateRequest.getNombre() != null) {
            preceptor.setNombre(updateRequest.getNombre());
            user.setName(updateRequest.getNombre());
            necesitaActualizarUsuario = true;
        }
        if (updateRequest.getApellido() != null) {
            preceptor.setApellido(updateRequest.getApellido());
            user.setLastName(updateRequest.getApellido());
            necesitaActualizarUsuario = true;
        }
        if (updateRequest.getGenero() != null) {
            preceptor.setGenero(updateRequest.getGenero());
        }
        if (updateRequest.getTipoDoc() != null) {
            preceptor.setTipoDoc(updateRequest.getTipoDoc());
        }
        if (updateRequest.getDni() != null) {
            preceptor.setDni(updateRequest.getDni());
        }
        if (updateRequest.getEmail() != null) {
            preceptor.setEmail(updateRequest.getEmail());
            user.setEmail(updateRequest.getEmail());
            necesitaActualizarUsuario = true;
        }
        if (updateRequest.getDireccion() != null) {
            preceptor.setDireccion(updateRequest.getDireccion());
        }
        if (updateRequest.getTelefono() != null) {
            preceptor.setTelefono(updateRequest.getTelefono());
        }
        if (updateRequest.getFechaNacimiento() != null) {
            validarFechas(updateRequest.getFechaNacimiento(), updateRequest.getFechaIngreso());
            preceptor.setFechaNacimiento(updateRequest.getFechaIngreso());

        }

        if (updateRequest.getFechaIngreso() != null) {
            validarFechas(updateRequest.getFechaNacimiento(), updateRequest.getFechaIngreso());
            preceptor.setFechaIngreso(updateRequest.getFechaIngreso());
        }


        if (necesitaActualizarUsuario) {
            preceptor.setUser(user);
        }
        preceptor = preceptorRepository.save(preceptor);

        logger.info("Se actualizo correctamente el preceptor " + preceptor.getId());
        return PreceptorResponse.builder()
                .preceptor(PreceptorMapper.toDto(preceptor))
                .code(0)
                .mensaje("Preceptor actualizado correctamente")
                .build();
    }

    @Override
    public PreceptorResponse deletePreceptor(Long id) {
        Preceptor preceptor = preceptorRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> new PreceptorException("No existe el preceptor id " + id));
        preceptor.setActive(false);
        preceptor.setUser(null);
        preceptorRepository.save(preceptor);

        logger.info("Se desactivó el preceptor {} y se eliminó su usuario asociado", id);

        return PreceptorResponse.builder()
                .preceptor(new PreceptorDto())
                .code(0)
                .mensaje("Se eliminó correctamente el usuario y se desactivó el preceptor")
                .build();
    }

    @Override
    public PreceptorResponseList listPreceptores() {
        List<PreceptorDto> preceptores = preceptorRepository.findByActiveIsTrue().stream()
                .map(PreceptorMapper::toDto)
                .collect(Collectors.toList());
        return PreceptorResponseList.builder()
                .preceptores(preceptores)
                .code(0)
                .mensaje("Se listaron correctamente los preceptores")
                .build();
    }

    @Override
    public PreceptorResponseList listAllPreceptores() {
        List<PreceptorDto> preceptores = preceptorRepository.findAll().stream()
                .map(PreceptorMapper::toDto)
                .collect(Collectors.toList());
        return PreceptorResponseList.builder()
                .preceptores(preceptores)
                .code(0)
                .mensaje("Se listaron correctamente los preceptores")
                .build();
    }

    private void validarFechas(Date nacimiento, Date ingreso) {
        Date actual = new Date();
        if (nacimiento != null && nacimiento.after(actual)) {
            throw new PreceptorException("La fecha de nacimiento debe ser anterior a la actual");
        }
        if (ingreso != null && ingreso.after(actual)) {
            throw new PreceptorException("La fecha de ingreso debe ser anterior a la actual");
        }
        if (nacimiento != null && ingreso != null && !ingreso.after(nacimiento)) {
            throw new PreceptorException("La fecha de ingreso debe ser posterior a la fecha de nacimiento");
        }
    }
}
