package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.service.AlumnoReactivacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlumnoReactivacionServiceImpl implements AlumnoReactivacionService {
    
    private final AlumnoRepository alumnoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final HistorialMateriaRepository historialMateriaRepository;
    private final CalificacionRepository calificacionRepository;
    
    @Override
    @Transactional
    public AlumnoResponse reactivarAlumno(Long alumnoId) {
        log.info("Iniciando reactivación de alumno ID: {}", alumnoId);
        
        // Buscar alumno
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new AlumnoException("Alumno no encontrado"));
        
        // Verificar que esté excluido por repetición
        if (alumno.getEstado() != EstadoAlumno.EXCLUIDO_POR_REPETICION) {
            throw new AlumnoException("El alumno no está excluido por repetición. Estado actual: " + alumno.getEstado());
        }
        
        // Obtener el último historial curso (el que causó la exclusión)
        List<HistorialCurso> historiales = historialCursoRepository.findByAlumno_IdOrderByFechaDesdeDesc(alumno.getId());
        
        if (historiales.isEmpty()) {
            throw new AlumnoException("No se encontró historial de cursos para el alumno");
        }
        
        HistorialCurso ultimoHistorial = historiales.get(0);
        Curso ultimoCurso = ultimoHistorial.getCurso();
        
        log.info("Limpiando datos del último curso: {} para alumno {}", 
                ultimoCurso.getAnio() + "° " + ultimoCurso.getDivision(), alumno.getDni());
        
        // 1. Eliminar calificaciones del último curso
        limpiarCalificacionesUltimoCurso(ultimoHistorial);
        
        // 2. Eliminar historial materias del último curso
        historialMateriaRepository.deleteByHistorialCurso_Id(ultimoHistorial.getId());
        
        // 3. Eliminar el último historial curso
        historialCursoRepository.delete(ultimoHistorial);
        
        // 4. Reactivar alumno
        alumno.setEstado(EstadoAlumno.REGULAR);
        alumno.setCursoActual(ultimoCurso); // Vuelve al mismo curso pero sin historial
        alumno.setCantidadRepeticiones(0); // Reset repeticiones
        alumnoRepository.save(alumno);
        
        log.info("Alumno {} reactivado exitosamente. Vuelve a {}", 
                alumno.getDni(), ultimoCurso.getAnio() + "° " + ultimoCurso.getDivision());
        
        return AlumnoResponse.builder()
                .alumno(null) // No incluir DTO completo por seguridad
                .code(0)
                .mensaje(String.format("Alumno reactivado exitosamente. Vuelve a %d° %s. " +
                        "Se eliminaron las calificaciones del curso anterior pero se mantiene el historial de otros cursos.",
                        ultimoCurso.getAnio(), ultimoCurso.getDivision()))
                .build();
    }
    
    private void limpiarCalificacionesUltimoCurso(HistorialCurso historialCurso) {
        // Obtener todas las historias de materias del curso
        List<HistorialMateria> historialMaterias = historialMateriaRepository
                .findAllByHistorialCursoId(historialCurso.getId());
        
        // Eliminar calificaciones de cada materia del curso
        for (HistorialMateria hm : historialMaterias) {
            List<Calificacion> calificaciones = calificacionRepository.findByHistorialMateria_Id(hm.getId());
            if (!calificaciones.isEmpty()) {
                calificacionRepository.deleteAll(calificaciones);
                log.debug("Eliminadas {} calificaciones de materia {} para historial {}", 
                        calificaciones.size(), 
                        hm.getMateriaCurso().getMateria().getNombre(),
                        hm.getId());
            }
        }
    }
}