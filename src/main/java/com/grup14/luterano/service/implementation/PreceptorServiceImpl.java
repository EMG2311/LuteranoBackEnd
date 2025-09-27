package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.PreceptorDto;
import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.exeptions.PreceptorException;
import com.grup14.luterano.exeptions.PreceptorException;
import com.grup14.luterano.mappers.PreceptorMapper;
import com.grup14.luterano.mappers.PreceptorMapper;
import com.grup14.luterano.repository.PreceptorRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.Preceptor.PreceptorRequest;
import com.grup14.luterano.request.Preceptor.PreceptorUpdateRequest;
import com.grup14.luterano.response.Preceptor.PreceptorResponse;
import com.grup14.luterano.response.Preceptor.PreceptorResponseList;
import com.grup14.luterano.service.PreceptorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
        this.userRepository=userRepository;
    }

    @Override
    public PreceptorResponse crearPreceptor(PreceptorRequest request) {
        Optional<Preceptor> existentePorEmail = preceptorRepository.findByEmail(request.getEmail());
        Optional<Preceptor> existentePorDni = preceptorRepository.findByDni(request.getDni());
        Optional<User> existeUser = userRepository.findByEmail(request.getEmail());
        if (existentePorEmail.isPresent() ) {
            throw new PreceptorException("Ya existe un preceptor registrado con ese email");
        }
        if (existentePorDni.isPresent() ) {
            throw new PreceptorException("Ya existe un preceptor registrado con ese DNI");
        }
        if(existeUser.isEmpty()){
            throw new PreceptorException("No existe un usuario con ese mail. Por favor crearlo y volver a intentar");
        }
        if(!Rol.ROLE_PRECEPTOR.name().equals(existeUser.get().getRol().getName())){
            throw new PreceptorException("El usuario no tiene rol preceptor");
        }
        if(!existeUser.get().getName().equals(request.getNombre())
                || !existeUser.get().getLastName().equals(request.getApellido())){
            existeUser.get().setName(request.getNombre());
            existeUser.get().setLastName(request.getApellido());
        }

        validarFechas(request.getFechaNacimiento(),request.getFechaIngreso());

        Preceptor preceptor =  Preceptor.builder()
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
                .user(existeUser.get())
                .build();

        preceptorRepository.save(preceptor);
        logger.info("Se creo correctamente el preceptor {} {}", preceptor.getNombre(), preceptor.getApellido());
        return PreceptorResponse.builder()
                .preceptor(PreceptorMapper.toDto(preceptor))
                .code(0)
                .mensaje("Se creo correctamente el preceptor")
                .build();
    }

    @Override
    public PreceptorResponse updatePreceptor(PreceptorUpdateRequest updateRequest) {
        Preceptor preceptor = preceptorRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new PreceptorException("No existe preceptor con id: " + updateRequest.getId()));
        User user=preceptor.getUser();
        boolean necesitaActualizarUsuario=false;
        if (user == null) {
            throw new RuntimeException("El preceptor no tiene usuario asociado");
        }

        if (updateRequest.getNombre() != null) {
            preceptor.setNombre(updateRequest.getNombre());
            user.setName(updateRequest.getNombre());
            necesitaActualizarUsuario=true;
        }
        if (updateRequest.getApellido() != null) {
            preceptor.setApellido(updateRequest.getApellido());
            user.setLastName(updateRequest.getApellido());
            necesitaActualizarUsuario=true;
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
            necesitaActualizarUsuario=true;
        }
        if (updateRequest.getDireccion() != null) {
            preceptor.setDireccion(updateRequest.getDireccion());
        }
        if (updateRequest.getTelefono() != null) {
            preceptor.setTelefono(updateRequest.getTelefono());
        }
        if (updateRequest.getFechaNacimiento() != null) {
           validarFechas(updateRequest.getFechaNacimiento(),updateRequest.getFechaIngreso());
            preceptor.setFechaNacimiento(updateRequest.getFechaIngreso());

        }

        if (updateRequest.getFechaIngreso() != null) {
            validarFechas(updateRequest.getFechaNacimiento(),updateRequest.getFechaIngreso());
            preceptor.setFechaIngreso(updateRequest.getFechaIngreso());
        }


        if(necesitaActualizarUsuario){
            preceptor.setUser(user);
        }
        preceptor = preceptorRepository.save(preceptor);

        logger.info("Se actualizo correctamente el preceptor "+ preceptor.getId());
        return PreceptorResponse.builder()
                .preceptor(PreceptorMapper.toDto(preceptor))
                .code(0)
                .mensaje("Preceptor actualizado correctamente")
                .build();
    }

    @Override
    public PreceptorResponse deletePreceptor(Long id) {
        Preceptor preceptor = preceptorRepository.findById(id)
                .orElseThrow(() -> new PreceptorException("No existe preceptor con id " + id));
        preceptorRepository.delete(preceptor);
        logger.info("Preceptor eliminado: {}", id);
        return PreceptorResponse.builder()
                .preceptor(new PreceptorDto())
                .code(0)
                .mensaje("Se eliminó correctamente el preceptor")
                .build();
    }

    @Override
    public PreceptorResponseList listPreceptores() {
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

    @Override
    public PreceptorResponse findPreceptorByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new PreceptorException("No existe un usuario con el id " + userId));
        Preceptor preceptor = preceptorRepository.findByUser(user)
                .orElseThrow(()->new PreceptorException("No hay ningun preceptor asociado al usuario "+ user.getUsername()));

        return PreceptorResponse.builder()
                .preceptor(PreceptorMapper.toDto(preceptor))
                .mensaje("")
                .code(0)
                .build();
    }
}
