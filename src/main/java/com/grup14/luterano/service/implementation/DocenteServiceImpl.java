package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.DocenteDto;
import com.grup14.luterano.dto.MateriaCursoDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.mappers.DocenteMapper;
import com.grup14.luterano.mappers.MateriaMapper;
import com.grup14.luterano.repository.DocenteRepository;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.repository.MateriaRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.docente.DocenteRequest;
import com.grup14.luterano.request.docente.DocenteUpdateRequest;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.response.docente.DocenteResponseList;
import com.grup14.luterano.service.DocenteService;
import com.grup14.luterano.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocenteServiceImpl implements DocenteService {

    private final DocenteRepository docenteRepository;

    private final MateriaRepository materiaRepository;

    private final UserRepository userRepository;

    private final MateriaCursoRepository materiaCursoRepository;

    private static final Logger logger = LoggerFactory.getLogger(DocenteServiceImpl.class);
    public DocenteServiceImpl(DocenteRepository docenteRepository,
                              MateriaRepository materiaRepository,
                              UserRepository userRepository, MateriaCursoRepository materiaCursoRepository) {
        this.docenteRepository = docenteRepository;
        this.materiaRepository = materiaRepository;
        this.userRepository = userRepository;
        this.materiaCursoRepository = materiaCursoRepository;
    }


    @Override
    @Transactional
    public DocenteResponse crearDocente(DocenteRequest docenteRequest) {
        Optional<Docente> existentePorEmail = docenteRepository.findByEmail(docenteRequest.getEmail());
        Optional<Docente> existentePorDni = docenteRepository.findByDni(docenteRequest.getDni());
        Optional<User> existeUser = userRepository.findByEmail(docenteRequest.getEmail());
        if (existentePorEmail.isPresent() ) {
            throw new DocenteException("Ya existe un docente registrado con ese email");
        }
        if (existentePorDni.isPresent() ) {
            throw new DocenteException("Ya existe un docente registrado con ese DNI");
        }
        if(existeUser.isEmpty()){
            throw new DocenteException("No existe un usuario con ese mail. Por favor crearlo y volver a intentar");
        }
        if(!Rol.ROLE_DOCENTE.name().equals(existeUser.get().getRol().getName())){
            throw new DocenteException("El usuario no tiene rol docente");
        }
        if(!existeUser.get().getName().equals(docenteRequest.getNombre())
        || !existeUser.get().getLastName().equals(docenteRequest.getApellido())){
            existeUser.get().setName(docenteRequest.getNombre());
            existeUser.get().setLastName(docenteRequest.getApellido());
        }

        validarFechaAnteriorAActual(docenteRequest.getFechaNacimiento()
                ,"La fecha de nacimiento debe ser anterior a la fecha actual");
        validarFechaAnteriorAActual(docenteRequest.getFechaIngreso(),
                "La fecha de ingreso debe ser anterior a la fecha actual");
        validarFechaPosterior(docenteRequest.getFechaIngreso(), docenteRequest.getFechaNacimiento(),
                "La fecha de ingreso debe ser posterior a la fecha de nacimiento");

        Docente docente =  Docente.builder()
                .nombre(docenteRequest.getNombre())
                .apellido(docenteRequest.getApellido())
                .genero(docenteRequest.getGenero())
                .tipoDoc(docenteRequest.getTipoDoc())
                .dni(docenteRequest.getDni())
                .email(docenteRequest.getEmail())
                .direccion(docenteRequest.getDireccion())
                .telefono(docenteRequest.getTelefono())
                .fechaNacimiento(docenteRequest.getFechaNacimiento())
                .fechaIngreso(docenteRequest.getFechaIngreso())
                .user(existeUser.get())
                .build();

        docenteRepository.save(docente);
        logger.info("Se creo correctamente el docente {} {}", docente.getNombre(), docente.getApellido());
        return DocenteResponse.builder()
                .docente(DocenteMapper.toDto(docente))
                .code(0)
                .mensaje("Se creo correctamente el docente")
                .build();
    }

    @Override
    public DocenteResponse updateDocente(DocenteUpdateRequest updateRequest) {
        Docente docente = docenteRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new DocenteException("No existe docente con id: " + updateRequest.getId()));
        User user=docente.getUser();
        boolean necesitaActualizarUsuario=false;
        if (user == null) {
            throw new RuntimeException("El docente no tiene usuario asociado");
        }

        if (updateRequest.getNombre() != null) {
            docente.setNombre(updateRequest.getNombre());
            user.setName(updateRequest.getNombre());
            necesitaActualizarUsuario=true;
        }
        if (updateRequest.getApellido() != null) {
            docente.setApellido(updateRequest.getApellido());
            user.setLastName(updateRequest.getApellido());
            necesitaActualizarUsuario=true;
        }
        if (updateRequest.getGenero() != null) {
            docente.setGenero(updateRequest.getGenero());
        }
        if (updateRequest.getTipoDoc() != null) {
            docente.setTipoDoc(updateRequest.getTipoDoc());
        }
        if (updateRequest.getDni() != null) {
            docente.setDni(updateRequest.getDni());
        }
        if (updateRequest.getEmail() != null) {
            docente.setEmail(updateRequest.getEmail());
            user.setEmail(updateRequest.getEmail());
            necesitaActualizarUsuario=true;
        }
        if (updateRequest.getDireccion() != null) {
            docente.setDireccion(updateRequest.getDireccion());
        }
        if (updateRequest.getTelefono() != null) {
            docente.setTelefono(updateRequest.getTelefono());
        }
        if (updateRequest.getFechaNacimiento() != null) {
            validarFechaAnteriorAActual(updateRequest.getFechaNacimiento(),
                    "La fecha de nacimiento debe ser anterior a la fecha actual");

            validarFechaPosterior(docente.getFechaIngreso(), updateRequest.getFechaNacimiento(),
                    "La fecha de nacimiento debe ser anterior a la fecha de ingreso");

            docente.setFechaNacimiento(updateRequest.getFechaNacimiento());
        }

        if (updateRequest.getFechaIngreso() != null) {
            validarFechaAnteriorAActual(updateRequest.getFechaIngreso(),
                    "La fecha de ingreso debe ser anterior a la fecha actual");

            validarFechaPosterior(updateRequest.getFechaIngreso(), docente.getFechaNacimiento(),
                    "La fecha de ingreso debe ser posterior a la fecha de nacimiento");

            docente.setFechaIngreso(updateRequest.getFechaIngreso());
        }


        if(necesitaActualizarUsuario){
            docente.setUser(user); //Por si se hicieron cambios en el usuario
        }
        docente = docenteRepository.save(docente);

        logger.info("Se actualizo correctamente el docente "+ docente.getId());
        return DocenteResponse.builder()
                .docente(DocenteMapper.toDto(docente))
                .code(0)
                .mensaje("Docente actualizado correctamente")
                .build();
    }

    private void validarFechaAnteriorAActual(Date fecha, String mensajeError) throws DocenteException {
        Date fechaActual = new Date();
        if (fecha.after(fechaActual)) {
            throw new DocenteException(mensajeError);
        }
    }

    private void validarFechaPosterior(Date fechaPosterior, Date fechaAnterior, String mensajeError) throws DocenteException {
        if (!fechaPosterior.after(fechaAnterior)) {
            throw new DocenteException(mensajeError);
        }
    }


    @Override
    @Transactional
    public DocenteResponse deleteDocente(Long id) {
        Docente docente = docenteRepository.findById(id).orElseThrow(() -> new DocenteException("No existe el docente id " + id));
        docenteRepository.deleteById(id);
        logger.info("Se elimino correctamente el docente " + id);
        return DocenteResponse.builder()
                .docente(new DocenteDto())
                .code(0)
                .mensaje("Se elimino correctamente el docente")
                .build();
    }

    @Override
    public DocenteResponseList listDocentes() {
        List<DocenteDto> docentes = new ArrayList<>();
        docenteRepository.findAll().forEach(docente->{
            docentes.add(DocenteMapper.toDto(docente));
        });
        return DocenteResponseList.builder()
                .docenteDtos(docentes)
                .code(0)
                .mensaje("Se listo correctamente los docentes")
                .build();
    }


}
