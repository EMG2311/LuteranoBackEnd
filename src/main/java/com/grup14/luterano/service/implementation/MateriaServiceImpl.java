package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.exeptions.MateriaException;
import com.grup14.luterano.mappers.MateriaMapper;
import com.grup14.luterano.repository.MateriaRepository;
import com.grup14.luterano.request.materia.MateriaRequest;
import com.grup14.luterano.request.materia.MateriaUpdateRequest;
import com.grup14.luterano.response.Materia.MateriaResponse;
import com.grup14.luterano.response.Materia.MateriaResponseList;
import com.grup14.luterano.service.MateriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class MateriaServiceImpl implements MateriaService {

    private final MateriaRepository materiaRepository;
    private final Logger logger = LoggerFactory.getLogger(MateriaServiceImpl.class);

    public MateriaServiceImpl(MateriaRepository materiaRepository){
        this.materiaRepository=materiaRepository;
    }

    @Override
    public MateriaResponse crearMateria(MateriaRequest materiaRequest) {
        Materia materia = new Materia();
        materia.setNombreMateria(materiaRequest.getNombreMateria());
        materia.setDescipcion(materiaRequest.getDescripcion());
        materia.setNivel(materiaRequest.getNivel());
        Optional<Materia> existente = materiaRepository.findByNombreMateria(materia.getNombreMateria());
        if(existente.isPresent()){
            throw new MateriaException("Ya existe una materia con ese nombre");
        }
        materiaRepository.save(materia);

        logger.info("Materia creada: {}", materia.getNombreMateria());

        return MateriaResponse.builder()
                .code(0)
                .mensaje("Materia creada correctamente")
                .materiaDto(MateriaMapper.toDto(materia))
                .build();
    }

    @Override
    public MateriaResponse updateMateria(MateriaUpdateRequest request) {
        Materia materia = materiaRepository.findById(request.getId())
                .orElseThrow(() -> new MateriaException("No se encontró la materia con ID: " + request.getId()));

        if (!request.getNombreMateria().isEmpty()) {
            materia.setNombreMateria(request.getNombreMateria());
        }

        if (!request.getDescripcion().isEmpty()) {
            materia.setDescipcion(request.getDescripcion());
        }

        if (request.getNivel()!=null) {
            materia.setNivel(request.getNivel());
        }

        materiaRepository.save(materia);

        logger.info("Materia actualizada: {}", materia.getNombreMateria());

        return MateriaResponse.builder()
                .code(0)
                .mensaje("Materia actualizada correctamente")
                .materiaDto(MateriaMapper.toDto(materia))
                .build();
    }

    @Override
    public MateriaResponseList listarMaterias() {
        List<Materia> materias = materiaRepository.findAll();

        List<MateriaDto> materiaDtos = materias.stream()
                .map(MateriaMapper::toDto)
                .collect(Collectors.toList());

        return MateriaResponseList.builder()
                .code(0)
                .mensaje("Listado de materias")
                .materiasDto(materiaDtos)
                .build();
    }

    @Override
    public MateriaResponse borrarMateria(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new MateriaException("No se encontró la materia con ID: " + id));

        materiaRepository.delete(materia);

        logger.info("Materia eliminada: {}", materia.getNombreMateria());

        return MateriaResponse.builder()
                .code(0)
                .mensaje("Materia eliminada correctamente")
                .materiaDto(new MateriaDto())
                .build();
    }
}
