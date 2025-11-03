package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Calificacion;
import com.grup14.luterano.entities.HistorialMateria;
import com.grup14.luterano.exeptions.CalificacionException;
import com.grup14.luterano.mappers.CalificacionMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.calificacion.CalificacionRequest;
import com.grup14.luterano.request.calificacion.CalificacionUpdateRequest;
import com.grup14.luterano.response.calificaciones.CalificacionListResponse;
import com.grup14.luterano.response.calificaciones.CalificacionResponse;
import com.grup14.luterano.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

    private final AlumnoRepository alumnoRepo;
    private final MateriaCursoRepository materiaCursoRepo;
    private final CicloLectivoRepository cicloRepo;
    private final HistorialCursoRepository historialCursoRepo;
    private final HistorialMateriaRepository historialMateriaRepo;
    private final CalificacionRepository calificacionRepo;
    private final CursoRepository cursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;


    @Transactional
    public CalificacionResponse crearCalificacion(CalificacionRequest req) {
        var alumno = alumnoRepo.findById(req.getAlumnoId())
                .orElseThrow(() -> new CalificacionException("Alumno no encontrado"));

        LocalDate fechaNota = (req.getFecha() != null) ? req.getFecha() : LocalDate.now();

        var ciclo = cicloRepo.findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqual(fechaNota, fechaNota)
                .orElseThrow(() -> new CalificacionException("No hay ciclo lectivo para la fecha " + fechaNota));

        var hc = historialCursoRepo.findVigenteEnFecha(alumno.getId(), ciclo.getId(), fechaNota)
                .orElseThrow(() -> new CalificacionException("El alumno no cursaba en la fecha " + fechaNota));

        var mc = materiaCursoRepo.findByMateriaIdAndCursoId(req.getMateriaId(), hc.getCurso().getId())
                .orElseThrow(() -> new CalificacionException("El curso del alumno no dicta esa materia"));

        var hm = historialMateriaRepo.findByHistorialCurso_IdAndMateriaCurso_Id(hc.getId(), mc.getId())
                .orElseGet(() -> historialMateriaRepo.save(
                        HistorialMateria.builder().historialCurso(hc).materiaCurso(mc).build()
                ));

        if (calificacionRepo.existsByHistorialMateria_IdAndEtapaAndNumeroNota(hm.getId(), req.getEtapa(), req.getNumeroNota())) {
            throw new CalificacionException("Ya existe la nota " + req.getNumeroNota() + " en etapa " + req.getEtapa());
        }

        var cal = Calificacion.builder()
                .historialMateria(hm)
                .etapa(req.getEtapa())
                .numeroNota(req.getNumeroNota())
                .nota(req.getNota())
                .fecha(fechaNota)
                .build();

        cal = calificacionRepo.save(cal);

        return CalificacionResponse.builder()
                .calificacion(CalificacionMapper.toDto(cal))
                .code(0)
                .mensaje("Se registró correctamente la calificación")
                .build();
    }


    @Transactional(readOnly = true)
    public CalificacionResponse obtener(Long alumnoId, Long materiaId, Long califId) {

        var cal = calificacionRepo.findOwned(alumnoId, materiaId, califId)
                .orElseThrow(() -> new CalificacionException("Calificación no encontrada para ese alumno y materia"));
        return CalificacionResponse.builder()
                .calificacion(CalificacionMapper.toDto(cal))
                .code(0)
                .mensaje("Ok")
                .build();

    }

    @Transactional
    public CalificacionResponse actualizar(CalificacionUpdateRequest req) {
        var cal = calificacionRepo.findOwned(req.getAlumnoId(), req.getMateriaId(), req.getCalifId())
                .orElseThrow(() -> new CalificacionException(
                        "Calificación no encontrada para ese alumno y materia"));
        if (req.getNota() != null) {
            cal.setNota(req.getNota());
        }

        if (req.getFecha() != null) {
            cal.setFecha(req.getFecha());
        }

        cal = calificacionRepo.save(cal);

        return CalificacionResponse.builder()
                .calificacion(CalificacionMapper.toDto(cal))
                .code(0)
                .mensaje("Calificación actualizada correctamente")
                .build();
    }

    @Transactional
    public CalificacionResponse eliminar(Long alumnoId, Long materiaId, Long califId) {
        var cal = calificacionRepo.findOwned(alumnoId, materiaId, califId)
                .orElseThrow(() -> new CalificacionException("Calificación no encontrada para ese alumno y materia"));

        calificacionRepo.delete(cal);

        return CalificacionResponse.builder()
                .calificacion(null)
                .code(0)
                .mensaje("Calificación eliminada correctamente")
                .build();
    }

    @Transactional(readOnly = true)
    public CalificacionListResponse listarPorAnio(Long alumnoId, int anio) {
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        var list = calificacionRepo.findByAlumnoAndAnio(alumnoId, desde, hasta)
                .stream().map(CalificacionMapper::toDto).toList();

        return CalificacionListResponse.builder()
                .calificaciones(list)
                .code(0)
                .mensaje("Ok")
                .build();
    }

    @Transactional(readOnly = true)
    public CalificacionListResponse listarPorAnioYEtapa(Long alumnoId, int anio, int etapa) {
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        var list = calificacionRepo.findByAlumnoAndAnioAndEtapa(alumnoId, etapa, desde, hasta)
                .stream().map(CalificacionMapper::toDto).toList();

        return CalificacionListResponse.builder()
                .calificaciones(list)
                .code(0)
                .mensaje("Ok")
                .build();
    }

    @Transactional(readOnly = true)
    public CalificacionListResponse listarPorMateria(Long alumnoId, Long materiaId) {

        boolean cursaOMaterializoHM =
                historialMateriaRepo.existsByHistorialCurso_Alumno_IdAndMateriaCurso_Materia_Id(alumnoId, materiaId)
                        || historialCursoRepo.existsAlumnoCursoMateria(alumnoId, materiaId);

        if (!cursaOMaterializoHM) {
            throw new CalificacionException("El alumno no cursó esa materia.");
        }

        var list = calificacionRepo.findByAlumnoAndMateria(alumnoId, materiaId)
                .stream().map(CalificacionMapper::toDto).toList();

        return CalificacionListResponse.builder()
                .calificaciones(list)
                .code(0)
                .mensaje("Ok")
                .build();
    }


}