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
import jakarta.transaction.Transactional;
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


    @Transactional
    public MateriaResponse crearMateria(MateriaRequest materiaRequest) {
        // Validar que no exista otra materia con mismo nombre
        boolean exists = materiaRepository.existsByNombre(materiaRequest.getNombre());
        if (exists) {
            throw new RuntimeException("Ya existe una materia con ese nombre");
        }

        Materia materia = Materia.builder()
                .nombre(materiaRequest.getNombre())
                .descripcion(materiaRequest.getDescripcion())
                .nivel(materiaRequest.getNivel())
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
        List<Materia> materias = materiaRepository.findAll();

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

        materiaRepository.delete(materia);
        return MateriaResponse.builder()
                .materiaDto(null)
                .code(0)
                .mensaje("Se elimino correctamente la materia")
                .build();
    }
}

