package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.DocenteDto;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.repository.DocenteRepository;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class DocenteServiceImpl implements DocenteService {
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private UserService userService;


    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DocenteServiceImpl.class);



    @Override
    @Transactional
    public DocenteResponse crearDocente(DocenteRequest docenteRequest) {
        Optional<Docente> existentePorEmail = docenteRepository.findByEmail(docenteRequest.getEmail());
        Optional<Docente> existentePorDni = docenteRepository.findByDni(docenteRequest.getDni());
        Optional<User> existeUser = userRepository.findByEmail(docenteRequest.getEmail());
        if (existentePorEmail.isPresent() || existentePorDni.isPresent()) {
            throw new DocenteException("Ya existe un docente registrado con ese email o DNI");
        }
        if(existeUser.isEmpty()){
            throw new DocenteException("No existe un usuario con ese mail. Por favor crearlo y volver a intentar");
        }
        if(!existeUser.get().getName().equals(docenteRequest.getNombre())
        || !existeUser.get().getLastName().equals(docenteRequest.getApellido())){
            existeUser.get().setName(docenteRequest.getNombre());
            existeUser.get().setLastName(docenteRequest.getApellido());
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
                .user(existeUser.get())
                .build();

        docenteRepository.save(docente);
        logger.info("Se creo correctamente el docente {} {}", docente.getNombre(), docente.getApellido());
        return DocenteResponse.builder()
                .docente(DocenteDto.builder()
                        .id(docente.getId())
                        .nombre(docente.getNombre())
                        .apellido(docente.getApellido())
                        .genero(docente.getGenero())
                        .tipoDoc(docente.getTipoDoc())
                        .dni(docente.getDni())
                        .email(docente.getEmail())
                        .direccion(docente.getDireccion())
                        .telefono(docente.getTelefono())
                        .fechaNacimiento(docente.getFechaNacimiento())
                        .fechaIngreso(docente.getFechaIngreso())
                        .materias(docente.getMaterias())
                        .build())
                .code(0)
                .mensaje("Se creo correcatmente el docente")
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
            docente.setFechaNacimiento(updateRequest.getFechaNacimiento());
        }
        if (updateRequest.getFechaIngreso() != null) {
            docente.setFechaIngreso(updateRequest.getFechaIngreso());
        }

        docente.setUser(user); //Por si se hicieron cambios en el usuario
        docente = docenteRepository.save(docente);

        logger.info("Se actualizo correctamente el docente "+ docente.getId());
        return DocenteResponse.builder()
                .docente(DocenteDto.builder()
                        .id(docente.getId())
                        .nombre(docente.getNombre())
                        .apellido(docente.getApellido())
                        .genero(docente.getGenero())
                        .tipoDoc(docente.getTipoDoc())
                        .dni(docente.getDni())
                        .email(docente.getEmail())
                        .direccion(docente.getDireccion())
                        .telefono(docente.getTelefono())
                        .fechaNacimiento(docente.getFechaNacimiento())
                        .fechaIngreso(docente.getFechaIngreso()).build())
                .code(0)
                .mensaje("Docente actualizado correctamente")
                .build();
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
    public DocenteResponse asignarMateria(Long docenteId, Long materiaId) {
        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado con ID: " + docenteId));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new EntityNotFoundException("Materia no encontrada con ID: " + materiaId));

        if (docente.getMaterias() == null) {
            docente.setMaterias(new HashSet<>());
        }

        if (!docente.getMaterias().contains(materia)) {
            docente.getMaterias().add(materia);
            docente = docenteRepository.save(docente);
        }
        DocenteDto docenteDto = DocenteDto.builder()
                .nombre(docente.getNombre())
                .apellido(docente.getApellido())
                .genero(docente.getGenero())
                .tipoDoc(docente.getTipoDoc())
                .dni(docente.getDni())
                .email(docente.getEmail())
                .direccion(docente.getDireccion())
                .telefono(docente.getTelefono())
                .fechaNacimiento(docente.getFechaNacimiento())
                .fechaIngreso(docente.getFechaIngreso())
                .materias(docente.getMaterias())
                .build();

        logger.info("Se asigno correctamente la materia" + materiaId + " para el docente "+ docenteId);

        return DocenteResponse.builder()
                .docente(docenteDto)
                .code(0)
                .mensaje("Materia asignada correctamente")
                .build();
    }

    @Override
    public DocenteResponse desasignarMateria(Long docenteId, Long materiaId) {
        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado con ID: " + docenteId));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new EntityNotFoundException("Materia no encontrada con ID: " + materiaId));

        if (docente.getMaterias() != null && docente.getMaterias().contains(materia)) {
            docente.getMaterias().remove(materia);
            docenteRepository.save(docente);
        }

        logger.info("Se desasigno la materia "+materiaId+" del docente "+docenteId);
        DocenteDto docenteDto = DocenteDto.builder()
                .nombre(docente.getNombre())
                .apellido(docente.getApellido())
                .genero(docente.getGenero())
                .tipoDoc(docente.getTipoDoc())
                .dni(docente.getDni())
                .email(docente.getEmail())
                .direccion(docente.getDireccion())
                .telefono(docente.getTelefono())
                .fechaNacimiento(docente.getFechaNacimiento())
                .fechaIngreso(docente.getFechaIngreso())
                .materias(docente.getMaterias())
                .build();

        return DocenteResponse.builder()
                .docente(docenteDto)
                .code(0)
                .mensaje("Materia desasignada correctamente")
                .build();
    }

    @Override
    public DocenteResponseList listDocentes() {
        List<DocenteDto> docentes = new ArrayList<>();
        docenteRepository.findAll().forEach(docente->{
            docentes.add(DocenteDto.builder()
                    .id(docente.getId())
                    .nombre(docente.getNombre())
                    .apellido(docente.getApellido())
                    .genero(docente.getGenero())
                    .tipoDoc(docente.getTipoDoc())
                    .dni(docente.getDni())
                    .email(docente.getEmail())
                    .direccion(docente.getDireccion())
                    .telefono(docente.getTelefono())
                    .fechaNacimiento(docente.getFechaNacimiento())
                    .fechaIngreso(docente.getFechaIngreso())
                    .materias(docente.getMaterias())
                    .build());
        });
        return DocenteResponseList.builder()
                .docenteDtos(docentes)
                .code(0)
                .mensaje("Se listo correctamente los docentes")
                .build();
    }


}
