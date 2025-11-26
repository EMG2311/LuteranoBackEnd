package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.exeptions.CursoException;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.curso.CursoRequest;
import com.grup14.luterano.request.curso.CursoUpdateRequest;
import com.grup14.luterano.request.curso.IntercambiarAulasRequest;
import com.grup14.luterano.response.curso.CursoResponse;
import com.grup14.luterano.response.curso.CursoResponseList;
import com.grup14.luterano.service.CursoService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CursoSeriviceImpl implements CursoService {

    private final CursoRepository cursoRepository;

    private final AulaRepository aulaRepository;
    private final PreceptorRepository preceptorRepository;
    private final UserRepository userRepository;
    private final DocenteRepository docenteRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final AlumnoRepository alumnoRepository;
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


        if (cursoUpdateRequest.getAnio() != null) curso.setAnio(cursoUpdateRequest.getAnio());
        if (cursoUpdateRequest.getDivision() != null) curso.setDivision(cursoUpdateRequest.getDivision());
        if (cursoUpdateRequest.getNivel() != null) curso.setNivel(cursoUpdateRequest.getNivel());


        if (cursoUpdateRequest.getAulaId() != null) {
            Aula aula = aulaRepository.findById(cursoUpdateRequest.getAulaId())
                    .orElseThrow(() -> new CursoException("Aula no encontrada con ID: " + cursoUpdateRequest.getAulaId()));

            if (aula.getCurso() != null && !aula.getCurso().equals(curso)) {
                throw new CursoException("El aula ya está asignada a otro curso.");
            }

            curso.setAula(aula);
            aula.setCurso(curso);
        } else if (cursoUpdateRequest.getAulaId() == null && curso.getAula() != null) {

            Aula aulaActual = curso.getAula();
            aulaActual.setCurso(null);
            curso.setAula(null);
        }

        curso = cursoRepository.save(curso);
        logger.info("Curso actualizado con ID: {}", curso.getId());


        return CursoResponse.builder()
                .curso(CursoMapper.toDto(curso))
                .code(0)
                .mensaje("Curso actualizado correctamente")
                .build();
    }

    @Override
    @Transactional
    public CursoResponse deleteCurso(Long id) {
        var curso = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoException("Curso no encontrado con ID: " + id));

        // 1) Política con alumnos: impedir borrar si hay asignados
        var alumnos = alumnoRepository.findByCursoActual_Id(id);
        if (!alumnos.isEmpty()) {
            throw new CursoException("No se puede eliminar: tiene alumnos asignados (" + alumnos.size() + ").");
        }

        // 2) Romper 1-1 con Aula (ambos lados)
        if (curso.getAula() != null) {
            var aula = curso.getAula();
            curso.setAula(null);
            if (aula.getCurso() != null) aula.setCurso(null);
            aulaRepository.save(aula); // opcional pero prolijo
        }

        // 3) Borrar dictados (1-N) del lado hijo
        materiaCursoRepository.deleteByCursoId(id);
        // No hace falta clear() si borraste via repo, pero no molesta:
        curso.getDictados().clear();

        // 4) Romper N-1 con Preceptor usando helper del otro lado
        if (curso.getPreceptor() != null) {
            curso.getPreceptor().removeCurso(curso); // quita de la lista y hace setPreceptor(null)
        }

        // 5) Guardar limpieza y eliminar
        cursoRepository.save(curso);
        cursoRepository.delete(curso);

        return CursoResponse.builder()
                .curso(new CursoDto())
                .code(0)
                .mensaje("Curso eliminado correctamente")
                .build();
    }

    @Override
    public CursoResponse getCursoById(Long id) {
        Curso curso = cursoRepository.findById(id).orElseThrow(() -> new CursoException("Curso no encontrado con ID: " + id));

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

    @Transactional(readOnly = true)
    public CursoResponseList listarCursosPorPreceptor(Long preceptorId) {

        preceptorRepository.findByIdAndActiveIsTrue(preceptorId).orElseThrow(() -> new CursoException("No hay ningun preceptor activo con ese Id"));

        List<Curso> cursos = cursoRepository.findByPreceptor_Id(preceptorId);


        return CursoResponseList.builder()
                .cursoDtos(cursos.stream().map(CursoMapper::toDto).toList())
                .code(200)
                .mensaje("OK")
                .build();
    }

    @Transactional(readOnly = true)
    public CursoResponseList listarCursosPorDocente(Long docenteId) {

        docenteRepository.findByIdAndActiveIsTrue(docenteId)
                .orElseThrow(() -> new CursoException("No hay ningún docente activo con ese Id"));

        java.util.List<Curso> cursos = cursoRepository.findByDocente_Id(docenteId);

        return CursoResponseList.builder()
                .cursoDtos(cursos.stream().map(CursoMapper::toDto).toList())
                .code(200)
                .mensaje("OK")
                .build();
    }
    @Override
    @Transactional
    public CursoResponse intercambiarAulas(IntercambiarAulasRequest req) {
        if (req.getCursoId1() == null || req.getCursoId2() == null) {
            throw new CursoException("Ambos IDs de curso son obligatorios");
        }
        if (req.getCursoId1().equals(req.getCursoId2())) {
            throw new CursoException("Los cursos deben ser distintos");
        }
        Curso curso1 = cursoRepository.findById(req.getCursoId1())
                .orElseThrow(() -> new CursoException("Curso no encontrado con ID: " + req.getCursoId1()));
        Curso curso2 = cursoRepository.findById(req.getCursoId2())
                .orElseThrow(() -> new CursoException("Curso no encontrado con ID: " + req.getCursoId2()));

        Aula aula1 = curso1.getAula();
        Aula aula2 = curso2.getAula();

        // Validar que ambos cursos tengan aula asignada
        if (aula1 == null && aula2 == null) {
            throw new CursoException("Ninguno de los cursos tiene aula asignada");
        }

        // Intercambiar las aulas
        curso1.setAula(aula2);
        if (aula2 != null) aula2.setCurso(curso1);
        curso2.setAula(aula1);
        if (aula1 != null) aula1.setCurso(curso2);

        cursoRepository.save(curso1);
        cursoRepository.save(curso2);
        if (aula1 != null) aulaRepository.save(aula1);
        if (aula2 != null) aulaRepository.save(aula2);

        return CursoResponse.builder()
                .curso(CursoMapper.toDto(curso1))
                .code(0)
                .mensaje("Aulas intercambiadas correctamente entre los cursos")
                .build();
    }

}

