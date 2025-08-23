package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.exeptions.AulaException;
import com.grup14.luterano.mappers.AlumnoMapper;
import com.grup14.luterano.mappers.AulaMapper;
import com.grup14.luterano.repository.AulaRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.MesaExamenRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.aula.AulaRequest;
import com.grup14.luterano.request.aula.AulaUpdateRequest;
import com.grup14.luterano.response.aula.AulaResponse;
import com.grup14.luterano.response.aula.AulaResponseList;
import com.grup14.luterano.service.AulaService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class AulaServiceImpl implements AulaService {

    @Autowired
    private CursoRepository cursoRepository;
    private AulaRepository aulaRepository;
    private MesaExamenRepository mesaExamenRepository;

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AulaServiceImpl.class);


    @Override
    @Transactional
    public AulaResponse crearAula(AulaRequest aulaRequest) {

    //Validar si ya existe un aula con el mismo nombre
        Optional<Aula> aulaExistente = aulaRepository.findByNombre(aulaRequest.getNombre());
        if (aulaExistente.isPresent()) {
            throw new AulaException("Ya existe un aula con el nombre: " + aulaRequest.getNombre());
        }

    //Validar exista curso
        Curso curso = cursoRepository.findById(aulaRequest.getCurso().getId())
                .orElseThrow(() -> new AulaException("El curso no existe"));


        //  Mapear el request a entidad usando el mapper
        Aula aula = AulaMapper.toEntity(aulaRequest);
        aula.setCurso(curso); // Asignar el curso a la nueva aula

        // Guardar la nueva entidad en la base de datos
        aulaRepository.save(aula);

        logger.info("Aula creada correctamente  con nombre: {}", aula.getNombre());

        // Devolver la respuesta
        return AulaResponse.builder()
                .aula(AulaMapper.toDto(aula))
                .code(0)
                .mensaje("Alumno creado correctamente")
                .build();

    }

    @Override
    @Transactional
    public AulaResponse updateAula(AulaUpdateRequest aulaUpdateRequest) {
        Aula aula = aulaRepository.findById(aulaUpdateRequest.getId())
                .orElseThrow(() -> new AulaException("No se encontró el aula con ID: " + aulaUpdateRequest.getId()));

        //  Actualizar campos
        if (!aulaUpdateRequest.getNombre().isEmpty()) aula.setNombre(aulaUpdateRequest.getNombre());
        if (!aulaUpdateRequest.getUbicacion().isEmpty()) aula.setUbicacion(aulaUpdateRequest.getUbicacion());
        if (aulaUpdateRequest.getCapacidad() != null) aula.setCapacidad(aulaUpdateRequest.getCapacidad());

        //  Guardar los cambios
        aula = aulaRepository.save(aula);
        logger.info("Aula actualizada: {} ", aula.getNombre());

        return AulaResponse.builder()
                .aula(AulaMapper.toDto(aula))
                .code(0)
                .mensaje("Aula actualizada correctamente")
                .build();

    }

    @Override
    public AulaResponse deleteAula(Long id) {

        Aula aula = aulaRepository.findById(id).orElseThrow(() -> new AulaException("No se encontró el aula con ID: " + id));

        // Verificar las dependencias que aula tenga asociada (curso o mesa de examen). En caso afirmativo, lanzar una excepción. UI: preguntar al usuario si desea eliminarla de todas formas.

        if (aula.getCurso() != null) throw new AulaException("El aula está asociada a un curso.");
        if (mesaExamenRepository.existsByAulaId(id)) throw new AulaException("El aula esta asociada mesa de examen.");


        aulaRepository.delete(aula);  // o deleteById(id);


        return null;
    }

    @Override
    public AulaResponse getAulaById(Long id) {
        return null;
    }

    @Override
    public AulaResponseList listAulas() {
        return null;
    }


}
