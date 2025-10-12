package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.AulaDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AulaServiceImpl implements AulaService {

    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private AulaRepository aulaRepository;
    @Autowired
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

        // Guardar la nueva entidad en la base de datos
        Aula aula = AulaMapper.toEntity(aulaRequest);

        aulaRepository.save(aula);

        logger.info("Aula creada correctamente  con nombre: {}", aula.getNombre());

        // Devolver la respuesta
        return AulaResponse.builder()
                .aula(AulaMapper.toDto(aula))
                .code(0)
                .mensaje("Aula creada correctamente")
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
        logger.info("Aula eliminada: {} ", aula.getNombre());
        return AulaResponse.builder()
                .aula(AulaMapper.toDto(aula))
                .code(0)
                .mensaje("Aula eliminada correctamente")
                .build();
    }

    @Override
    public AulaResponse getAulaById(Long id) {
        Aula aula = aulaRepository.findById(id).orElseThrow(() -> new AulaException("No se encontró el aula con ID: " + id));

        return AulaResponse.builder()
                .aula(AulaMapper.toDto(aula))
                .code(0)
                .mensaje("Aula encontrada correctamente")
                .build();
    }

    @Override
    public AulaResponseList listAulas() {
        List<AulaDto> aulas = aulaRepository.findAll().stream()
                .map(AulaMapper::toDto)
                .toList();

        return AulaResponseList.builder()
                .aulaDtos(aulas)
                .code(0)
                .mensaje("Aulas listadas correctamente")
                .build();
    }


    @Transactional(readOnly = true)
    public AulaResponseList listarAulasSinCurso() {
        List<Aula> aulas = aulaRepository.findByCursoIsNull();
        return AulaResponseList.builder()
                .aulaDtos(aulas.stream().map(AulaMapper::toDto).toList())
                .code(200)
                .mensaje("OK")
                .build();
    }


}
