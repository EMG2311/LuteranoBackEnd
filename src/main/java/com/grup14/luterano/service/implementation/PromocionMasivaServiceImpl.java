package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.promocion.AlumnoPromocionDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.promocion.PromocionMasivaRequest;
import com.grup14.luterano.response.promocion.PromocionMasivaResponse;
import com.grup14.luterano.service.NotaFinalService;
import com.grup14.luterano.service.PromocionMasivaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromocionMasivaServiceImpl implements PromocionMasivaService {

    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final NotaFinalService notaFinalService;

    @Override
    @Transactional
    public PromocionMasivaResponse ejecutarPromocionMasiva(PromocionMasivaRequest request) {
        log.info("Iniciando promoción masiva para año {} - DryRun: {}", request.getAnio(), request.getDryRun());

        // Validar que existe el ciclo lectivo
        CicloLectivo cicloLectivo = cicloLectivoRepository.findById(request.getCicloLectivoId())
                .orElseThrow(() -> new RuntimeException("Ciclo lectivo no encontrado"));

        // Obtener todos los alumnos activos (no egresados, borrados ni excluidos por repetición)
        List<Alumno> alumnosActivos = alumnoRepository.findByEstadoNotIn(
                List.of(EstadoAlumno.EGRESADO, EstadoAlumno.BORRADO, EstadoAlumno.EXCLUIDO_POR_REPETICION));

        List<AlumnoPromocionDto> resumen = new ArrayList<>();
        int promocionados = 0, repitentes = 0, egresados = 0, excluidos = 0, noProcesados = 0;

        for (Alumno alumno : alumnosActivos) {
            try {
                AlumnoPromocionDto resultado = procesarAlumno(alumno, request, cicloLectivo);
                resumen.add(resultado);

                switch (resultado.getAccion()) {
                    case "PROMOCIONADO" -> promocionados++;
                    case "REPITENTE" -> repitentes++;
                    case "EGRESADO" -> egresados++;
                    case "EXCLUIDO_POR_REPETICION" -> excluidos++;
                    case "NO_PROCESADO" -> noProcesados++;
                }

            } catch (Exception e) {
                log.error("Error procesando alumno {}: {}", alumno.getId(), e.getMessage());
                resumen.add(AlumnoPromocionDto.builder()
                        .alumnoId(alumno.getId())
                        .dni(alumno.getDni())
                        .apellido(alumno.getApellido())
                        .nombre(alumno.getNombre())
                        .cursoAnterior(formatearCurso(alumno.getCursoActual()))
                        .accion("NO_PROCESADO")
                        .motivo("Error: " + e.getMessage())
                        .build());
                noProcesados++;
            }
        }

        log.info("Promoción masiva completada - Procesados: {}, Promocionados: {}, Repitentes: {}, Egresados: {}, Excluidos: {}",
                resumen.size(), promocionados, repitentes, egresados, excluidos);

        return PromocionMasivaResponse.builder()
                .procesados(resumen.size())
                .promocionados(promocionados)
                .repitentes(repitentes)
                .egresados(egresados)
                .excluidos(excluidos)
                .noProcesados(noProcesados)
                .dryRun(request.getDryRun())
                .resumen(resumen)
                .code(0)
                .mensaje(String.format("Promoción masiva %s. Total: %d alumnos",
                        request.getDryRun() ? "simulada" : "ejecutada", resumen.size()))
                .build();
    }

    private AlumnoPromocionDto procesarAlumno(Alumno alumno, PromocionMasivaRequest request, CicloLectivo cicloLectivo) {
        String cursoAnterior = formatearCurso(alumno.getCursoActual());

        // Verificar si tiene historial curso en el ciclo lectivo
        Optional<HistorialCurso> historialOpt = historialCursoRepository
                .findByAlumno_IdAndCicloLectivo_IdAndFechaHastaIsNull(alumno.getId(), cicloLectivo.getId());

        if (historialOpt.isEmpty()) {
            return AlumnoPromocionDto.builder()
                    .alumnoId(alumno.getId())
                    .dni(alumno.getDni())
                    .apellido(alumno.getApellido())
                    .nombre(alumno.getNombre())
                    .cursoAnterior(cursoAnterior)
                    .accion("NO_PROCESADO")
                    .motivo("No tiene historial curso en el ciclo lectivo")
                    .repeticionesActuales(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                    .build();
        }

        HistorialCurso historial = historialOpt.get();
        Curso cursoActual = historial.getCurso();

        // Contar materias desaprobadas
        int materiasDesaprobadas = contarMateriasDesaprobadas(alumno.getId(), cursoActual.getId(), request.getAnio());

        // Aplicar reglas de promoción
        if (cursoActual.getAnio() == 6) {
            // 6to año -> Egresado
            return procesarEgreso(alumno, historial, request, cursoAnterior, materiasDesaprobadas);
        } else if (materiasDesaprobadas < 3) {
            // Menos de 3 materias -> Promociona
            return procesarPromocion(alumno, cursoActual, historial, request, cursoAnterior, materiasDesaprobadas);
        } else {
            // 3 o más materias -> Repite
            return procesarRepeticion(alumno, historial, request, cursoAnterior, materiasDesaprobadas);
        }
    }

    private int contarMateriasDesaprobadas(Long alumnoId, Long cursoId, int anio) {
        // Obtener todas las materias del curso
        List<MateriaCurso> materiasCurso = materiaCursoRepository.findByCursoId(cursoId);

        int desaprobadas = 0;
        for (MateriaCurso mc : materiasCurso) {
            Integer notaFinal = notaFinalService.calcularNotaFinal(alumnoId, mc.getMateria().getId(), anio);
            if (notaFinal == null || notaFinal < 6) {
                desaprobadas++;
            }
        }

        return desaprobadas;
    }

    private AlumnoPromocionDto procesarEgreso(Alumno alumno, HistorialCurso historial,
                                              PromocionMasivaRequest request, String cursoAnterior,
                                              int materiasDesaprobadas) {
        if (!request.getDryRun()) {
            // Cerrar historial curso
            historial.setFechaHasta(LocalDate.now());
            historialCursoRepository.save(historial);

            // Cambiar estado a egresado
            alumno.setEstado(EstadoAlumno.EGRESADO);
            alumno.setCursoActual(null);
            alumnoRepository.save(alumno);
        }

        return AlumnoPromocionDto.builder()
                .alumnoId(alumno.getId())
                .dni(alumno.getDni())
                .apellido(alumno.getApellido())
                .nombre(alumno.getNombre())
                .cursoAnterior(cursoAnterior)
                .cursoNuevo("EGRESADO")
                .accion("EGRESADO")
                .materiasDesaprobadas(materiasDesaprobadas)
                .repeticionesActuales(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                .build();
    }

    private AlumnoPromocionDto procesarPromocion(Alumno alumno, Curso cursoActual, HistorialCurso historial,
                                                 PromocionMasivaRequest request, String cursoAnterior,
                                                 int materiasDesaprobadas) {
        // Buscar curso del año siguiente
        Optional<Curso> cursoSiguienteOpt = cursoRepository
                .findByAnioAndDivision(cursoActual.getAnio() + 1, cursoActual.getDivision());

        if (cursoSiguienteOpt.isEmpty()) {
            return AlumnoPromocionDto.builder()
                    .alumnoId(alumno.getId())
                    .dni(alumno.getDni())
                    .apellido(alumno.getApellido())
                    .nombre(alumno.getNombre())
                    .cursoAnterior(cursoAnterior)
                    .accion("NO_PROCESADO")
                    .motivo("No existe curso del año siguiente")
                    .materiasDesaprobadas(materiasDesaprobadas)
                    .repeticionesActuales(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                    .build();
        }

        Curso cursoSiguiente = cursoSiguienteOpt.get();
        String cursoNuevo = formatearCurso(cursoSiguiente);

        if (!request.getDryRun()) {
            // Cerrar historial actual
            historial.setFechaHasta(LocalDate.now());
            historialCursoRepository.save(historial);

            // Crear nuevo historial para el curso siguiente
            // Usar el mismo ciclo lectivo que el historial actual (la promoción normalmente se hace al final del ciclo)
            HistorialCurso nuevoHistorial = HistorialCurso.builder()
                    .alumno(alumno)
                    .curso(cursoSiguiente)
                    .cicloLectivo(historial.getCicloLectivo()) // Usar el mismo ciclo lectivo
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(null) // Se cerrará cuando termine el ciclo lectivo
                    .build();
            historialCursoRepository.save(nuevoHistorial);

            // Actualizar curso actual del alumno
            alumno.setCursoActual(cursoSiguiente);
            alumno.setCantidadRepeticiones(0); // Reset repeticiones al promocionar
            alumnoRepository.save(alumno);
        }

        return AlumnoPromocionDto.builder()
                .alumnoId(alumno.getId())
                .dni(alumno.getDni())
                .apellido(alumno.getApellido())
                .nombre(alumno.getNombre())
                .cursoAnterior(cursoAnterior)
                .cursoNuevo(cursoNuevo)
                .accion("PROMOCIONADO")
                .materiasDesaprobadas(materiasDesaprobadas)
                .repeticionesActuales(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                .build();
    }

    private AlumnoPromocionDto procesarRepeticion(Alumno alumno, HistorialCurso historial,
                                                  PromocionMasivaRequest request, String cursoAnterior,
                                                  int materiasDesaprobadas) {

        // Validar y obtener repeticiones actuales (manejar nulls para datos legacy)
        Integer repeticionesActuales = alumno.getCantidadRepeticiones();
        if (repeticionesActuales == null) {
            repeticionesActuales = 0;
            // Actualizar el alumno con el valor por defecto
            alumno.setCantidadRepeticiones(0);
            if (!request.getDryRun()) {
                alumnoRepository.save(alumno);
            }
        }

        int maxRepeticiones = request.getMaxRepeticiones();

        if (repeticionesActuales >= maxRepeticiones) {
            // Excluir alumno por exceso de repeticiones
            if (!request.getDryRun()) {
                // Cerrar historial actual
                historial.setFechaHasta(LocalDate.now());
                historialCursoRepository.save(historial);

                // Cambiar estado a excluido
                alumno.setEstado(EstadoAlumno.EXCLUIDO_POR_REPETICION);
                alumno.setCursoActual(null); // Quitar curso actual
                alumnoRepository.save(alumno);
            }

            return AlumnoPromocionDto.builder()
                    .alumnoId(alumno.getId())
                    .dni(alumno.getDni())
                    .apellido(alumno.getApellido())
                    .nombre(alumno.getNombre())
                    .cursoAnterior(cursoAnterior)
                    .cursoNuevo("EXCLUIDO")
                    .accion("EXCLUIDO_POR_REPETICION")
                    .motivo("Excede límite de repeticiones (" + maxRepeticiones + ")")
                    .materiasDesaprobadas(materiasDesaprobadas)
                    .repeticionesActuales(repeticionesActuales)
                    .build();
        }

        if (!request.getDryRun()) {
            // Cerrar historial actual
            historial.setFechaHasta(LocalDate.now());
            historialCursoRepository.save(historial);

            // Incrementar contador de repeticiones
            alumno.setCantidadRepeticiones(repeticionesActuales + 1);
            alumnoRepository.save(alumno);

            // El alumno se queda en el mismo curso
        }

        return AlumnoPromocionDto.builder()
                .alumnoId(alumno.getId())
                .dni(alumno.getDni())
                .apellido(alumno.getApellido())
                .nombre(alumno.getNombre())
                .cursoAnterior(cursoAnterior)
                .cursoNuevo(cursoAnterior + " (Repite)")
                .accion("REPITENTE")
                .materiasDesaprobadas(materiasDesaprobadas)
                .repeticionesActuales(repeticionesActuales + 1)
                .build();
    }

    private String formatearCurso(Curso curso) {
        if (curso == null) return "Sin curso";
        return curso.getAnio() + "° " + curso.getDivision().name();
    }
}