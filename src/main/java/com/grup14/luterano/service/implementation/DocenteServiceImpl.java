package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.repository.DocenteRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.docente.DocenteRequest;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.service.DocenteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class DocenteServiceImpl implements DocenteService {
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DocenteServiceImpl.class);



    @Override
    public DocenteResponse crearDocente(DocenteRequest docenteRequest) {
        Optional<Docente> existentePorEmail = docenteRepository.findByEmail(docenteRequest.getEmail());
        Optional<Docente> existentePorDni = docenteRepository.findByDni(docenteRequest.getDni());
        Optional<User> existeUser = userRepository.findByEmail(docenteRequest.getEmail());
        if (existentePorEmail.isPresent() || existentePorDni.isPresent()) {
            logger.error("Ya existe un docente registrado con ese email o DNI");
            throw new DocenteException("Ya existe un docente registrado con ese email o DNI");
        }
        if(existeUser.isEmpty()){
            logger.error("No existe un usuario con ese mail. Por favor crearlo y volver a intentar");
            throw new DocenteException("No existe un usuario con ese mail. Por favor crearlo y volver a intentar");
        }

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
                .materias(docenteRequest.getMaterias())
                .build();

        docenteRepository.save(docente);

        return docenteRequest.toResponse("Docente creado correctamente",0);
    }

    @Override
    public DocenteResponse updateDocente(DocenteRequest docenteRequest) {
        return null;
    }

    @Override
    public DocenteResponse deleteDocente(Long id) {
        return null;
    }

    @Override
    public DocenteResponse asignarMateria(Long docenteId, Long materiaId) {
        return null;
    }

    @Override
    public DocenteResponse desasignarMateria(Long docenteId, Long materiaId) {
        return null;
    }
}
