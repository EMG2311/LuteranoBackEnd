package com.grup14.luterano;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.HistorialCursoRepository;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.request.calificacion.CalificacionRequest;
import com.grup14.luterano.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Profile("seed-calificaciones") // activar con: -Dspring.profiles.active=seed-calificaciones
@RequiredArgsConstructor
public class CalificacionSeeder implements CommandLineRunner {

    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final CalificacionService calificacionService;

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final int ETAPAS = 2; // Etapa 1 y 2
    private static final int NOTAS_POR_ETAPA = 4; // 4 notas por etapa

    @Override
    public void run(String... args) {
        log.info("==> Iniciando siembra de calificaciones aleatorias para todos los cursos...");
        
        try {
            // Obtener todos los cursos
            var cursos = cursoRepository.findAll();
            log.info("Encontrados {} cursos para procesar", cursos.size());
            
            int totalCalificacionesCreadas = 0;
            int totalCalificacionesExistentes = 0;
            
            for (var curso : cursos) {
                log.info("Procesando curso: {} {} División {}", 
                    curso.getAnio(), curso.getNivel(), curso.getDivision());
                
                // Obtener alumnos activos del curso
                List<Alumno> alumnos = alumnoRepository.findByCursoActual_IdAndEstadoNot(
                    curso.getId(), EstadoAlumno.EGRESADO);
                
                if (alumnos.isEmpty()) {
                    log.warn("No se encontraron alumnos activos para el curso ID: {}", curso.getId());
                    continue;
                }
                
                log.info("Encontrados {} alumnos activos en el curso", alumnos.size());
                
                // Obtener materias del curso
                List<MateriaCurso> materiasCurso = materiaCursoRepository.findByCursoId(curso.getId());
                
                if (materiasCurso.isEmpty()) {
                    log.warn("No se encontraron materias asignadas al curso ID: {}", curso.getId());
                    continue;
                }
                
                log.info("Encontradas {} materias para el curso", materiasCurso.size());
                
                // Procesar cada alumno
                for (Alumno alumno : alumnos) {
                    // Buscar el historial curso activo del alumno
                    var historialCursoOpt = historialCursoRepository.findByAlumno_IdAndFechaHastaIsNull(alumno.getId());
                    
                    if (historialCursoOpt.isEmpty()) {
                        log.warn("No se encontró historial curso activo para alumno ID: {} ({})", 
                            alumno.getId(), alumno.getNombre() + " " + alumno.getApellido());
                        continue;
                    }
                    
                    // Procesar cada materia
                    for (MateriaCurso materiaCurso : materiasCurso) {
                        // Generar calificaciones para cada etapa y número de nota
                        for (int etapa = 1; etapa <= ETAPAS; etapa++) {
                            for (int numeroNota = 1; numeroNota <= NOTAS_POR_ETAPA; numeroNota++) {
                                
                                // Generar nota aleatoria entre 1 y 10
                                int notaAleatoria = RANDOM.nextInt(1, 11);
                                
                                // Usar la fecha de hoy para todas las calificaciones
                                LocalDate fechaEvaluacion = LocalDate.now();
                                
                                // Crear la calificación usando el servicio (se encarga de todas las validaciones)
                                CalificacionRequest request = CalificacionRequest.builder()
                                    .alumnoId(alumno.getId())
                                    .materiaId(materiaCurso.getMateria().getId())
                                    .nota(notaAleatoria)
                                    .etapa(etapa)
                                    .numeroNota(numeroNota)
                                    .fecha(fechaEvaluacion)
                                    .build();
                                
                                try {
                                    var response = calificacionService.crearCalificacion(request);
                                    if (response != null && (response.getCode() == null || response.getCode() >= 0)) {
                                        totalCalificacionesCreadas++;
                                        log.debug("Calificación creada: {} {} - {} - etapa {} nota {} = {}", 
                                            alumno.getNombre(), alumno.getApellido(),
                                            materiaCurso.getMateria().getNombre(),
                                            etapa, numeroNota, notaAleatoria);
                                    } else {
                                        // Probablemente ya existe la calificación
                                        totalCalificacionesExistentes++;
                                        log.debug("Calificación ya existe o error: {} {} - {} - etapa {} nota {}: {}", 
                                            alumno.getNombre(), alumno.getApellido(),
                                            materiaCurso.getMateria().getNombre(),
                                            etapa, numeroNota, 
                                            response != null ? response.getMensaje() : "Response nulo");
                                    }
                                } catch (Exception e) {
                                    // Contar como existente para no mostrar como error grave
                                    totalCalificacionesExistentes++;
                                    log.debug("Excepción al crear calificación para alumno {} en materia {} (etapa {} nota {}): {}", 
                                        alumno.getNombre(), 
                                        materiaCurso.getMateria().getNombre(),
                                        etapa, numeroNota, e.getMessage());
                                }
                            }
                        }
                    }
                }
                
                log.info("Terminado el procesamiento del curso: {} {} División {}", 
                    curso.getAnio(), curso.getNivel(), curso.getDivision());
            }
            
            log.info("==> Siembra de calificaciones finalizada.");
            log.info("Total de calificaciones nuevas creadas: {}", totalCalificacionesCreadas);
            log.info("Total de calificaciones que ya existían: {}", totalCalificacionesExistentes);
            
        } catch (Exception e) {
            log.error("Error durante la siembra de calificaciones: {}", e.getMessage(), e);
        }
    }
}