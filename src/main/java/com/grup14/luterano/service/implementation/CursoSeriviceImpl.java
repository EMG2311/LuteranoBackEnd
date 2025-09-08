package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.exeptions.CursoException;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.mappers.MateriaCursoMapper;
import com.grup14.luterano.repository.AulaRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.curso.CursoRequest;
import com.grup14.luterano.request.curso.CursoUpdateRequest;
import com.grup14.luterano.response.curso.CursoResponse;
import com.grup14.luterano.response.curso.CursoResponseList;
import com.grup14.luterano.service.CursoService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CursoSeriviceImpl implements CursoService {

    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CursoSeriviceImpl.class);


    @Override
    @Transactional
    public CursoResponse crearCurso(CursoRequest cursoRequest) {

        //  Validar si ya existe un curso con la misma combinación de atributos
        Optional<Curso> cursoExistente = cursoRepository.findByAnioAndDivisionAndNivel(
                cursoRequest.getAnio(), cursoRequest.getDivision(), cursoRequest.getNivel());
        if (cursoExistente.isPresent()) {
            throw new CursoException("Ya existe un curso con el mismo número, división y nivel.");
        }

        //  Mapear el request a entidad usando el mapper.Crea una nueva entidad 'Curso' con los datos del request
        Curso curso = CursoMapper.toEntity(cursoRequest);

        //  Validar y buscar el aula si se proporciona un ID
        Aula aula = null;
        if (cursoRequest.getAulaId() != null) {
            aula = aulaRepository.findById(cursoRequest.getAulaId())
                    .orElseThrow(() -> new CursoException("Aula no encontrada con ID: " + cursoRequest.getAulaId()));

        //  Validar que el aula no esté asignada a otro curso
            if (aula.getCurso() != null) {
                throw new CursoException("El aula ya está asignada a otro curso.");
            }

            //Asignar el aula al curso y establecer la relación bidireccional
            curso.setAula(aula);
            aula.setCurso(curso);
        }

        //  Guardar el curso
        Curso nuevoCurso = cursoRepository.save(curso);

        logger.info("Curso creado con ID: {}", nuevoCurso.getId());

        //  Retornar respuesta  usando el mapper
        return CursoResponse.builder()
                .curso(CursoMapper.toDto(nuevoCurso))
                .code(0)
                .mensaje("Curso creado correctamente")
                .build();
    }

    @Override
    @Transactional
    public CursoResponse updateCurso(CursoUpdateRequest cursoUpdateRequest) {
        //  Buscar el curso por su ID y lanzar excepción si no existe
        Curso curso = cursoRepository.findById(cursoUpdateRequest.getId())
                .orElseThrow(() -> new CursoException("Curso no encontrado con ID: " + cursoUpdateRequest.getId()));

        //  Actualizar campos
        if (cursoUpdateRequest.getAnio() != null) curso.setAnio(cursoUpdateRequest.getAnio());
        if (cursoUpdateRequest.getDivision() != null) curso.setDivision(cursoUpdateRequest.getDivision());
        if (cursoUpdateRequest.getNivel() != null) curso.setNivel(cursoUpdateRequest.getNivel());

        // Manejar la asignación de aula
        if (cursoUpdateRequest.getAulaId() != null) {
            Aula aula = aulaRepository.findById(cursoUpdateRequest.getAulaId())
                    .orElseThrow(() -> new CursoException("Aula no encontrada con ID: " + cursoUpdateRequest.getAulaId()));

            if (aula.getCurso() != null && !aula.getCurso().equals(curso)) {
                throw new CursoException("El aula ya está asignada a otro curso.");
            }

            curso.setAula(aula);
            aula.setCurso(curso);
        } else if (cursoUpdateRequest.getAulaId() == null && curso.getAula() != null) {
            // Si el aulaId es null y el curso tenía un aula, la desasignamos
            Aula aulaActual = curso.getAula();
            aulaActual.setCurso(null);
            curso.setAula(null);
        }

        //  Guardar los cambios
        curso = cursoRepository.save(curso);
        logger.info("Curso actualizado con ID: {}", curso.getId());

        // Retornar respuesta usando el mapper
        return CursoResponse.builder()
                .curso(CursoMapper.toDto(curso))
                .code(0)
                .mensaje("Curso actualizado correctamente")
                .build();


    }

    @Override
    public CursoResponse deleteCurso(Long id) {
        Curso curso = cursoRepository.findById(id).orElseThrow(() -> new CursoException("Curso no encontrado con ID: " + id));

        cursoRepository.deleteById(id);
        logger.info("Curso eliminado con ID: {}", id);
        return CursoResponse.builder()
                .curso(new CursoDto())
                .code(0)
                .mensaje("Curso eliminado correctamente")
                .build();

    }

    @Override
    public CursoResponse getCursoById(Long id) {
        Curso curso = cursoRepository.findById(id).orElseThrow(() -> new CursoException("Curso no encontrado con ID: " + id));

        // Retornar respuesta usando el mapper
        return CursoResponse.builder()
                .curso(CursoMapper.toDto(curso))
                .code(0)
                .mensaje("Curso encontrado correctamente")
                .build();
    }

    @Override
    public CursoResponseList listCursos() {
        List<CursoDto> cursos = cursoRepository.findAll()
                .stream()
                .map(CursoMapper::toDto)
                .collect(Collectors.toList());


        // Retornar la respuesta con la lista de cursos
        return CursoResponseList.builder()
                .cursoDtos(cursos)
                .code(0)
                .mensaje("Lista de cursos obtenida correctamente")
                .build();
    }

}

