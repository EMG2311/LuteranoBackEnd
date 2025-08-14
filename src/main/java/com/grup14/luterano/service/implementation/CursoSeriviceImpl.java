package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.exeptions.CursoException;
import com.grup14.luterano.mappers.AlumnoMapper;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.mappers.MateriaMapper;
import com.grup14.luterano.repository.AulaRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.curso.CursoRequest;
import com.grup14.luterano.request.curso.CursoUpdateRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.curso.CursoResponse;
import com.grup14.luterano.service.CursoService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CursoSeriviceImpl implements CursoService {

    @Autowired
    private CursoRepository cursoRepository;
    private AulaRepository aulaRepository;

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AlumnoServiceImpl.class);


    @Override
    @Transactional
    public CursoResponse crearCurso(CursoRequest cursoRequest) {

        //  Validar si ya existe un curso con la misma combinación de atributos
        Optional<Curso> cursoExistente = cursoRepository.findByNumeroAndDivisionAndNivel(
                cursoRequest.getNumero(), cursoRequest.getDivision(), cursoRequest.getNivel());
        if (cursoExistente.isPresent()) {
            throw new CursoException("Ya existe un curso con el mismo número, división y nivel.");
        }


        //  Validar y buscar el aula si se proporciona un ID
        Aula aula = null;
        if (cursoRequest.getId() != null) {
            aula = aulaRepository.findById(cursoRequest.getId())
                    .orElseThrow(() -> new CursoException("Aula no encontrada con ID: " + cursoRequest.getId()));
        }

        //  Mapear el request a entidad usando el mapper
        Curso curso = CursoMapper.toEntity(cursoRequest);
        curso.setAula(aula);
        // Lista de materias que asigna al objeto 'curso'
        curso.setMaterias(cursoRequest.getMaterias() != null ?
                cursoRequest.getMaterias().stream().map(MateriaMapper::toEntity).collect(Collectors.toList()) : null);

        //  Guardar el curso
        cursoRepository.save(curso);

        logger.info("Curso creado con ID: {}", curso.getId());

        //  Retornar respuesta usando el mapper
        return CursoResponse.builder()
                .curso(CursoMapper.toDto(curso))
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
        if (cursoUpdateRequest.getNumero() != null) curso.setNumero(cursoUpdateRequest.getNumero());
        if (cursoUpdateRequest.getDivision() != null) curso.setDivision(cursoUpdateRequest.getDivision());
        if (cursoUpdateRequest.getNivel() != null) curso.setNivel(cursoUpdateRequest.getNivel());


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

        // Verificar si el curso tiene alumnos asignados
//if (!curso.getAlumnos().isEmpty()) {
            //throw new CursoException("No se puede eliminar el curso porque tiene alumnos asignados.");
       // }

        // Eliminar el curso
        cursoRepository.deleteById(id);
        logger.info("Curso eliminado con ID: {}", id);
        return CursoResponse.builder()
                .curso(new CursoDto())
                .code(0)
                .mensaje("Curso eliminado correctamente")
                .build();

    }


}

