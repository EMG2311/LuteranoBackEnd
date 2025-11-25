package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.promocion.AlumnoPromocionDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.entities.enums.EstadoMateriaAlumno;
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
    private final HistorialMateriaRepository historialMateriaRepository;


    @Override
    @Transactional
    public PromocionMasivaResponse ejecutarPromocionMasiva(PromocionMasivaRequest request) {
        log.info("año: "+request.getAnio());
        log.info("Iniciando promoción masiva para año {} - DryRun: {}", request.getAnio(), request.getDryRun());

        // Validar que existe el ciclo lectivo
        CicloLectivo cicloLectivo = cicloLectivoRepository.findById(request.getCicloLectivoId())
                .orElseThrow(() -> new RuntimeException("Ciclo lectivo no encontrado"));

        // Obtener todos los alumnos activos (no egresados, borrados ni excluidos por repetición)
        List<Alumno> alumnosActivos = alumnoRepository.findByEstadoNotIn(
                List.of(EstadoAlumno.EGRESADO, EstadoAlumno.BORRADO, EstadoAlumno.EXCLUIDO_POR_REPETICION, EstadoAlumno.EGRESADO_CON_PREVIAS));
 
        List<AlumnoPromocionDto> resumen = new ArrayList<>();
        int promocionados = 0, repitentes = 0, egresados = 0, excluidos = 0, noProcesados = 0;

        for (Alumno alumno : alumnosActivos) {
            try {
                AlumnoPromocionDto resultado = procesarAlumno(alumno, request, cicloLectivo);
                resumen.add(resultado);

                switch (resultado.getAccion()) {
                    case "PROMOCIONADO" -> promocionados++;
                    case "REPITENTE" -> repitentes++;
                    case "EGRESADO", "EGRESADO_CON_PREVIAS" -> egresados++;
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

        // Alumno sin curso actual → no procesado
        if (alumno.getCursoActual() == null) {
            return AlumnoPromocionDto.builder()
                    .alumnoId(alumno.getId())
                    .dni(alumno.getDni())
                    .apellido(alumno.getApellido())
                    .nombre(alumno.getNombre())
                    .cursoAnterior("Sin curso")
                    .accion("NO_PROCESADO")
                    .motivo("Alumno sin curso actual")
                    .repeticionesActuales(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                    .build();
        }

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

        // Obtener todas las materias del curso
        List<MateriaCurso> materiasCurso = materiaCursoRepository.findByCursoId(cursoActual.getId());
        log.info("PromoMasiva - Alumno {} ({}) curso {} - materiasCurso.size = {}",
                alumno.getId(), alumno.getApellido(), formatearCurso(cursoActual), materiasCurso.size());
        List<com.grup14.luterano.dto.promocion.MateriaEstadoFinalDto> materiasEstadoFinal = new ArrayList<>();

        int materiasDesaprobadas = 0;

        for (MateriaCurso mc : materiasCurso) {
            Long materiaId = mc.getMateria().getId();
            String materiaNombre = mc.getMateria().getNombre();

            Integer notaFinal = notaFinalService.calcularNotaFinal(alumno.getId(), materiaId, request.getAnio());

            com.grup14.luterano.entities.enums.EstadoMateriaAlumno estadoMateria;

            if (notaFinal != null && notaFinal >= 6) {
                estadoMateria = com.grup14.luterano.entities.enums.EstadoMateriaAlumno.APROBADA;
            } else {
                estadoMateria = com.grup14.luterano.entities.enums.EstadoMateriaAlumno.DESAPROBADA;
                materiasDesaprobadas++;
            }

            if (!request.getDryRun()) {
                historialMateriaRepository
                        .findByHistorialCurso_IdAndMateriaCurso_Id(historial.getId(), mc.getId())
                        .ifPresent(hm -> {
                            hm.setEstado(estadoMateria);   // estado final de la materia en ese año
                            historialMateriaRepository.save(hm);
                        });
            }

            materiasEstadoFinal.add(
                    com.grup14.luterano.dto.promocion.MateriaEstadoFinalDto.builder()
                            .materiaId(materiaId)
                            .materiaNombre(materiaNombre)
                            .notaFinal(notaFinal)
                            .estadoFinal(estadoMateria)
                            .build()
            );
        }

        int previasAnteriores = contarPreviasAnteriores(alumno.getId(), request.getAnio());

        int totalPendientes = materiasDesaprobadas + previasAnteriores;

        if (cursoActual.getAnio() == 6) {
            // 6to año -> Egresa (con o sin previas)
            AlumnoPromocionDto dto = procesarEgreso(alumno, historial, request, cursoAnterior, totalPendientes);
            dto.setMateriasEstadoFinal(materiasEstadoFinal);
            return dto;
        } else if (totalPendientes < 3) {
            // Menos de 3 pendientes (desaprobadas + previas) -> Promociona
            AlumnoPromocionDto dto = procesarPromocion(alumno, cursoActual, historial, request, cursoAnterior, totalPendientes);
            dto.setMateriasEstadoFinal(materiasEstadoFinal);
            return dto;
        } else {
            // 3 o más pendientes -> Repite / excluido
            AlumnoPromocionDto dto = procesarRepeticion(alumno, historial, request, cursoAnterior, totalPendientes);
            dto.setMateriasEstadoFinal(materiasEstadoFinal);
            return dto;
        }
    }

    private int contarPreviasAnteriores(Long alumnoId, int anioCorte) {
        int count = 0;
        List<HistorialCurso> historialCompleto = historialCursoRepository.findHistorialCompletoByAlumnoId(alumnoId);
        for (HistorialCurso hc : historialCompleto) {
            int anioCurso = hc.getCicloLectivo().getFechaDesde().getYear();
            if (anioCurso < anioCorte) {
                List<HistorialMateria> hms = historialMateriaRepository.findAllByHistorialCursoId(hc.getId());
                for (HistorialMateria hm : hms) {
                    if (hm.getEstado() == EstadoMateriaAlumno.DESAPROBADA) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private AlumnoPromocionDto procesarEgreso(Alumno alumno, HistorialCurso historial,
                                              PromocionMasivaRequest request, String cursoAnterior,
                                              int totalPendientes) {
        if (!request.getDryRun()) {
            // Cerrar historial curso
            historial.setFechaHasta(LocalDate.now());
            historialCursoRepository.save(historial);

            // Si tiene previas, marcar como EGRESADO_CON_PREVIAS; si no, EGRESADO
            if (totalPendientes > 0) {
                alumno.setEstado(EstadoAlumno.EGRESADO_CON_PREVIAS);
            } else {
                alumno.setEstado(EstadoAlumno.EGRESADO);
            }
            alumno.setCursoActual(null);
            alumnoRepository.save(alumno);
        }

        String accion = (totalPendientes > 0) ? "EGRESADO_CON_PREVIAS" : "EGRESADO";

        return AlumnoPromocionDto.builder()
                .alumnoId(alumno.getId())
                .dni(alumno.getDni())
                .apellido(alumno.getApellido())
                .nombre(alumno.getNombre())
                .cursoAnterior(cursoAnterior)
                .cursoNuevo("EGRESADO")
                .accion(accion)
                .materiasDesaprobadas(totalPendientes) // ahora representa TODAS las pendientes
                .repeticionesActuales(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                .build();
    }

    private AlumnoPromocionDto procesarPromocion(Alumno alumno, Curso cursoActual, HistorialCurso historial,
                                                 PromocionMasivaRequest request, String cursoAnterior,
                                                 int totalPendientes) {
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
                    .materiasDesaprobadas(totalPendientes)
                    .repeticionesActuales(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                    .build();
        }

        Curso cursoSiguiente = cursoSiguienteOpt.get();
        String cursoNuevo = formatearCurso(cursoSiguiente);

        if (!request.getDryRun()) {
            // Cerrar historial actual
            historial.setFechaHasta(LocalDate.now());
            historialCursoRepository.save(historial);

            // Obtener o crear el ciclo lectivo del año siguiente
            CicloLectivo cicloSiguiente = obtenerOcrearCicloLectivoSiguiente(historial.getCicloLectivo());

            // Crear nuevo historial para el curso siguiente
            HistorialCurso nuevoHistorial = HistorialCurso.builder()
                    .alumno(alumno)
                    .curso(cursoSiguiente)
                    .cicloLectivo(cicloSiguiente)
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(null)
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
                .materiasDesaprobadas(totalPendientes)
                .repeticionesActuales(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                .build();
    }

    private AlumnoPromocionDto procesarRepeticion(Alumno alumno, HistorialCurso historial,
                                                  PromocionMasivaRequest request, String cursoAnterior,
                                                  int totalPendientes) {

        Integer repeticionesActuales = alumno.getCantidadRepeticiones();
        if (repeticionesActuales == null) {
            repeticionesActuales = 0;
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
                alumno.setCursoActual(null);
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
                    .materiasDesaprobadas(totalPendientes)
                    .repeticionesActuales(repeticionesActuales)
                    .build();
        }

        if (!request.getDryRun()) {
            // Cerrar historial actual
            historial.setFechaHasta(LocalDate.now());
            historialCursoRepository.save(historial);

            // Obtener o crear el ciclo lectivo del año siguiente
            CicloLectivo cicloSiguiente = obtenerOcrearCicloLectivoSiguiente(historial.getCicloLectivo());

            // Incrementar contador de repeticiones
            alumno.setCantidadRepeticiones(repeticionesActuales + 1);

            // Crear nuevo historial curso para el mismo curso pero en el ciclo siguiente
            HistorialCurso nuevoHistorial = HistorialCurso.builder()
                    .alumno(alumno)
                    .curso(historial.getCurso())
                    .cicloLectivo(cicloSiguiente)
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(null)
                    .build();
            historialCursoRepository.save(nuevoHistorial);

            alumnoRepository.save(alumno);
        }

        return AlumnoPromocionDto.builder()
                .alumnoId(alumno.getId())
                .dni(alumno.getDni())
                .apellido(alumno.getApellido())
                .nombre(alumno.getNombre())
                .cursoAnterior(cursoAnterior)
                .cursoNuevo(cursoAnterior + " (Repite)")
                .accion("REPITENTE")
                .materiasDesaprobadas(totalPendientes)
                .repeticionesActuales(repeticionesActuales + 1)
                .build();
    }

    private String formatearCurso(Curso curso) {
        if (curso == null) return "Sin curso";
        return curso.getAnio() + "° " + curso.getDivision().name();
    }

    /**
     * Obtiene o crea el ciclo lectivo del año siguiente al ciclo actual.
     */
    private CicloLectivo obtenerOcrearCicloLectivoSiguiente(CicloLectivo cicloActual) {
        int anioSiguiente = cicloActual.getFechaDesde().getYear() + 1;

        Optional<CicloLectivo> cicloSiguienteOpt = cicloLectivoRepository.findByAnio(anioSiguiente);

        if (cicloSiguienteOpt.isPresent()) {
            log.debug("Ciclo lectivo {} ya existe", anioSiguiente);
            return cicloSiguienteOpt.get();
        }

        CicloLectivo nuevoCiclo = CicloLectivo.builder()
                .nombre("Ciclo Lectivo " + anioSiguiente)
                .fechaDesde(LocalDate.of(anioSiguiente, 1, 1))
                .fechaHasta(LocalDate.of(anioSiguiente, 12, 31))
                .build();

        CicloLectivo cicloCreado = cicloLectivoRepository.save(nuevoCiclo);
        log.info("Ciclo lectivo {} creado automáticamente durante la promoción", anioSiguiente);

        return cicloCreado;
    }
}
