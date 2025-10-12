package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.docente.DocenteDto;
import com.grup14.luterano.dto.materiaCurso.MateriaCursoLigeroDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.mappers.DocenteMapper;
import com.grup14.luterano.mappers.MateriaCursoMapper;
import com.grup14.luterano.repository.DocenteRepository;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.repository.MateriaRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.docente.DocenteRequest;
import com.grup14.luterano.request.docente.DocenteUpdateRequest;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.response.docente.DocenteResponseList;
import com.grup14.luterano.service.DocenteService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        docenteRepository.findByEmailAndActiveIsTrue(docenteRequest.getEmail())
                .ifPresent(d -> { throw new DocenteException("Ya existe un docente activo con ese email"); });
        docenteRepository.findByDniAndActiveIsTrue(docenteRequest.getDni())
                .ifPresent(d -> { throw new DocenteException("Ya existe un docente activo con ese DNI"); });

        Optional<Docente> docenteInactivo = docenteRepository.findByEmailAndActiveIsFalse(docenteRequest.getEmail());

        User user = userRepository.findByEmail(docenteRequest.getEmail())
                .orElseThrow(() -> new DocenteException("No existe un usuario con ese mail. Por favor crearlo y volver a intentar"));

        if (!Rol.ROLE_DOCENTE.name().equals(user.getRol().getName())) {
            throw new DocenteException("El usuario no tiene rol docente");
        }

        if (!user.getName().equals(docenteRequest.getNombre())
                || !user.getLastName().equals(docenteRequest.getApellido())) {
            user.setName(docenteRequest.getNombre());
            user.setLastName(docenteRequest.getApellido());
        }

        validarFechaAnteriorAActual(docenteRequest.getFechaNacimiento(),
                "La fecha de nacimiento debe ser anterior a la fecha actual");
        validarFechaAnteriorAActual(docenteRequest.getFechaIngreso(),
                "La fecha de ingreso debe ser anterior a la fecha actual");
        validarFechaPosterior(docenteRequest.getFechaIngreso(), docenteRequest.getFechaNacimiento(),
                "La fecha de ingreso debe ser posterior a la fecha de nacimiento");

        Docente docente;

        if (docenteInactivo.isPresent()) {
            docente = docenteInactivo.get();

            if (!docente.getDni().equals(docenteRequest.getDni())) {
                throw new DocenteException("Existe un docente inactivo con ese email pero con distinto DNI. No se puede reactivar." +
                        "El dni que estaba cargado es: "+ docente.getDni());
            }

            docente.setActive(true);
            docente.setNombre(docenteRequest.getNombre());
            docente.setApellido(docenteRequest.getApellido());
            docente.setGenero(docenteRequest.getGenero());
            docente.setTipoDoc(docenteRequest.getTipoDoc());
            docente.setDireccion(docenteRequest.getDireccion());
            docente.setTelefono(docenteRequest.getTelefono());
            docente.setFechaNacimiento(docenteRequest.getFechaNacimiento());
            docente.setFechaIngreso(docenteRequest.getFechaIngreso());
            docente.setUser(user);

        } else {
            docente = Docente.builder()
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
                    .user(user)
                    .active(true)
                    .build();
        }

        docenteRepository.save(docente);
        logger.info("Docente {} {} registrado/reactivado correctamente (id={})",
                docente.getNombre(), docente.getApellido(), docente.getId());

        return DocenteResponse.builder()
                .docente(DocenteMapper.toDto(docente))
                .code(0)
                .mensaje("Se registró/reactivó correctamente el docente")
                .build();
    }

    @Override
    public DocenteResponse updateDocente(DocenteUpdateRequest updateRequest) {
        Docente docente = docenteRepository.findByIdAndActiveIsTrue(updateRequest.getId())
                .orElseThrow(() -> new DocenteException("No existe docente activo con id: " + updateRequest.getId()));
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
        Docente docente = docenteRepository.findByIdAndActiveIsTrue(id).orElseThrow(() -> new DocenteException("No existe el docente id " + id));
        materiaCursoRepository.unassignDocenteFromAll(id);
        docente.setActive(false);
        docente.setUser(null);
        docenteRepository.save(docente);
        logger.info("Se desactivo correctamente el docente " + id);
        return DocenteResponse.builder()
                .docente(new DocenteDto())
                .code(0)
                .mensaje("Se elimino correctamente el docente")
                .build();
    }

    @Override
    public DocenteResponseList listDocentes() {
        List<DocenteDto> docentes = docenteRepository.findByActiveIsTrue().stream()
                .map(docente -> {
                    DocenteDto dto = DocenteMapper.toDto(docente);

                    if (docente.getDictados() != null) {
                        List<MateriaCursoLigeroDto> dictadosLigero = docente.getDictados().stream()
                                .map(MateriaCursoMapper::toLigeroDto)
                                .collect(Collectors.toList());

                        dto.setDictados(dictadosLigero);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return DocenteResponseList.builder()
                .docenteDtos(docentes)
                .code(0)
                .mensaje("Se listaron correctamente los docentes")
                .build();
    }

    @Override
    public DocenteResponseList listAllDocentes() {
        List<DocenteDto> docentes = docenteRepository.findByActiveIsTrue().stream()
                .map(docente -> {
                    DocenteDto dto = DocenteMapper.toDto(docente);

                    if (docente.getDictados() != null) {
                        List<MateriaCursoLigeroDto> dictadosLigero = docente.getDictados().stream()
                                .map(MateriaCursoMapper::toLigeroDto)
                                .collect(Collectors.toList());

                        dto.setDictados(dictadosLigero);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return DocenteResponseList.builder()
                .docenteDtos(docentes)
                .code(0)
                .mensaje("Se listaron correctamente los docentes")
                .build();
    }


}
