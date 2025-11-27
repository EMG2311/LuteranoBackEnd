package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.exeptions.MateriaException;
import com.grup14.luterano.mappers.MateriaMapper;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.repository.MateriaRepository;
import com.grup14.luterano.request.materia.MateriaRequest;
import com.grup14.luterano.request.materia.MateriaUpdateRequest;
import com.grup14.luterano.response.Materia.MateriaResponse;
import com.grup14.luterano.response.Materia.MateriaResponseList;
import com.grup14.luterano.service.MateriaService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class MateriaServiceImpl implements MateriaService {

    private final MateriaRepository materiaRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final Logger logger = LoggerFactory.getLogger(MateriaServiceImpl.class);

    public MateriaServiceImpl(MateriaRepository materiaRepository, MateriaCursoRepository materiaCursoRepository) {
        this.materiaRepository = materiaRepository;
        this.materiaCursoRepository = materiaCursoRepository;
    }


    @Transactional
    public MateriaResponse crearMateria(MateriaRequest materiaRequest) {
        // Si existe una materia activa con ese nombre, error
        boolean exists = materiaRepository.existsByNombreAndActivaTrue(materiaRequest.getNombreMateria());
        if (exists) {
            throw new RuntimeException("Ya existe una materia activa con ese nombre");
        }

        // Si existe una materia INACTIVA con ese nombre, la reactivamos
        Materia materiaInactiva = materiaRepository.findByNombreAndActivaFalse(materiaRequest.getNombreMateria());
        if (materiaInactiva != null) {
            materiaInactiva.setActiva(true);
            materiaInactiva.setDescripcion(materiaRequest.getDescripcion());
            materiaInactiva.setNivel(materiaRequest.getNivel());
            materiaRepository.save(materiaInactiva);
            return MateriaResponse.builder()
                    .materiaDto(MateriaMapper.toDto(materiaInactiva))
                    .code(0)
                    .mensaje("Materia reactivada correctamente")
                    .build();
        }

        // Si no existe, crear nueva
        Materia materia = Materia.builder()
                .nombre(materiaRequest.getNombreMateria())
                .descripcion(materiaRequest.getDescripcion())
                .nivel(materiaRequest.getNivel())
                .activa(true)
                .build();

        materia = materiaRepository.save(materia);
        return MateriaResponse.builder()
                .materiaDto(MateriaMapper.toDto(materia))
                .code(0)
                .mensaje("Se creo correctamente la materia")
                .build();
    }

    @Transactional
    public MateriaResponse updateMateria(MateriaUpdateRequest materiaUpdateRequest) {
        Materia materia = materiaRepository.findById(materiaUpdateRequest.getId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        if (materiaUpdateRequest.getNombreMateria() != null) {
            materia.setNombre(materiaUpdateRequest.getNombreMateria());
        }
        if (materiaUpdateRequest.getDescripcion() != null) {
            materia.setDescripcion(materiaUpdateRequest.getDescripcion());
        }
        if (materiaUpdateRequest.getNivel() != null) {
            materia.setNivel(materiaUpdateRequest.getNivel());
        }

        materia = materiaRepository.save(materia);
        return MateriaResponse.builder()
                .materiaDto(MateriaMapper.toDto(materia))
                .code(0)
                .mensaje("Se actualizo correctamente la materia")
                .build();
    }

        public MateriaResponseList listarMaterias() {
        List<Materia> materias = materiaRepository.findAllByActivaTrue();
        List<MateriaDto> materiasDto = materias.stream()
            .map(MateriaMapper::toDto)
            .toList();
        return new MateriaResponseList(
            materiasDto,
            0,
            "Lista de materias obtenida correctamente"
        );
        }

        @Transactional
        public MateriaResponse borrarMateria(Long id) {
        Materia materia = materiaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        if (!materia.isActiva()) {
            return MateriaResponse.builder()
                .materiaDto(MateriaMapper.toDto(materia))
                .code(0)
                .mensaje("La materia ya estaba inactiva")
                .build();
        }
        materia.setActiva(false);
        materiaRepository.save(materia);
        // Cerrar inscripciones: desasignar docentes de todos los MateriaCurso de esta materia
        materiaCursoRepository.cerrarInscripcionesPorMateria(materia.getId());
        return MateriaResponse.builder()
            .materiaDto(MateriaMapper.toDto(materia))
            .code(0)
            .mensaje("Se eliminó (lógicamente) la materia y se cerraron inscripciones")
            .build();
        }
        @Override
        public MateriaResponse getMateriaById(Long id){
            Materia materia = materiaRepository.findById(id)
                    .orElseThrow(() -> new MateriaException("Materia no encontrada"));
            return MateriaResponse.builder()
                    .materiaDto(MateriaMapper.toDto(materia))
                    .code(0)
                    .mensaje("Se eliminó (lógicamente) la materia y se cerraron inscripciones")
                    .build();
        }
}

