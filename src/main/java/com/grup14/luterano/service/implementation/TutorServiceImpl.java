package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.TutorDto;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.exeptions.TutorException;
import com.grup14.luterano.mappers.TutorMapper;
import com.grup14.luterano.repository.TutorRepository;
import com.grup14.luterano.request.tutor.TutorRequest;
import com.grup14.luterano.request.tutor.TutorUpdateRequest;
import com.grup14.luterano.response.tutor.TutorResponse;
import com.grup14.luterano.response.tutor.TutorResponseList;
import com.grup14.luterano.service.TutorService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TutorServiceImpl implements TutorService {

    private final TutorRepository tutorRepository;
    private static final Logger logger = LoggerFactory.getLogger(TutorServiceImpl.class);

    public TutorServiceImpl(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Override
    public TutorResponse crearTutor(TutorRequest request) {
        Optional<Tutor> existentePorDni = tutorRepository.findByDni(request.getDni());
        Optional<Tutor> existentePorEmail = tutorRepository.findByEmail(request.getEmail());
        if (existentePorDni.isPresent()) {
            throw new TutorException("Ya existe un tutor registrado con ese DNI");
        }
        if (existentePorEmail.isPresent()) {
            throw new TutorException("Ya existe un tutor registrado con ese email");
        }

        validarFechas(request.getFechaNacimiento(), request.getFechaIngreso());

        Tutor tutor = Tutor.builder()
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
                .build();

        tutorRepository.save(tutor);
        logger.info("Tutor creado: {} {}", tutor.getNombre(), tutor.getApellido());
        return TutorResponse.builder()
                .tutor(TutorMapper.toDto(tutor))
                .code(0)
                .mensaje("Se creó correctamente el tutor")
                .build();
    }

    @Override
    public TutorResponse updateTutor(TutorUpdateRequest request) {
        Tutor tutor = tutorRepository.findById(request.getId())
                .orElseThrow(() -> new TutorException("No existe tutor con id " + request.getId()));

        if (request.getNombre() != null) tutor.setNombre(request.getNombre());
        if (request.getApellido() != null) tutor.setApellido(request.getApellido());
        if (request.getGenero() != null) tutor.setGenero(request.getGenero());
        if (request.getTipoDoc() != null) tutor.setTipoDoc(request.getTipoDoc());
        if (request.getDni() != null) tutor.setDni(request.getDni());
        if (request.getEmail() != null) tutor.setEmail(request.getEmail());
        if (request.getDireccion() != null) tutor.setDireccion(request.getDireccion());
        if (request.getTelefono() != null) tutor.setTelefono(request.getTelefono());

        if (request.getFechaNacimiento() != null) tutor.setFechaNacimiento(request.getFechaNacimiento());
        if (request.getFechaIngreso() != null) tutor.setFechaIngreso(request.getFechaIngreso());
        validarFechas(request.getFechaNacimiento(), request.getFechaIngreso());
        tutorRepository.save(tutor);
        logger.info("Tutor actualizado: {}", tutor.getId());
        return TutorResponse.builder()
                .tutor(TutorMapper.toDto(tutor))
                .code(0)
                .mensaje("Tutor actualizado correctamente")
                .build();
    }

    @Override
    public TutorResponse deleteTutor(Long id) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new TutorException("No existe tutor con id " + id));
        tutorRepository.delete(tutor);
        logger.info("Tutor eliminado: {}", id);
        return TutorResponse.builder()
                .tutor(new TutorDto())
                .code(0)
                .mensaje("Se eliminó correctamente el tutor")
                .build();
    }

    @Override
    public TutorResponseList listTutores() {
        List<TutorDto> tutores = tutorRepository.findAll().stream()
                .map(TutorMapper::toDto)
                .collect(Collectors.toList());
        return TutorResponseList.builder()
                .tutores(tutores)
                .code(0)
                .mensaje("Se listaron correctamente los tutores")
                .build();
    }

    private void validarFechas(Date nacimiento, Date ingreso) {
        Date actual = new Date();
        if (nacimiento != null && nacimiento.after(actual)) {
            throw new TutorException("La fecha de nacimiento debe ser anterior a la actual");
        }
        if (ingreso != null && ingreso.after(actual)) {
            throw new TutorException("La fecha de ingreso debe ser anterior a la actual");
        }
        if (nacimiento != null && ingreso != null && !ingreso.after(nacimiento)) {
            throw new TutorException("La fecha de ingreso debe ser posterior a la fecha de nacimiento");
        }
    }
}
