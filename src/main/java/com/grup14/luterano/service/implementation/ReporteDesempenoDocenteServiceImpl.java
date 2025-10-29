package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteDesempenoDocente.DetalleDictadoDto;
import com.grup14.luterano.dto.reporteDesempenoDocente.FiltrosReporteDto;
import com.grup14.luterano.dto.reporteDesempenoDocente.ReporteDesempenoDocenteDto;
import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.exeptions.ReporteDesempenoDocenteException;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.ReporteDesempenoDocente.ReporteDesempenoDocenteFiltroRequest;
import com.grup14.luterano.response.reporteDesempeñoDocente.ReporteDesempenoDocenteResponse;
import com.grup14.luterano.service.ReporteDesempenoDocenteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReporteDesempenoDocenteServiceImpl implements ReporteDesempenoDocenteService {

    private final DocenteRepository docenteRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final AsistenciaDocenteRepository asistenciaDocenteRepository;
    private final CalificacionRepository calificacionRepository;
    private final CicloLectivoRepository cicloLectivoRepository;


    @Override
    public ReporteDesempenoDocenteResponse generarReporteDesempenoDocente(ReporteDesempenoDocenteFiltroRequest filtros) {

        // DETERMINAR EL CICLO LECTIVO O EL PERÍODO QUE CUBRE EL REPORTE

        final LocalDate fechaInicio;
        final LocalDate fechaFin;

        Integer anioFiltro = filtros.getCicloLectivoAnio();
        if (filtros.getCicloLectivoAnio() != null) {
            // Prioridad 1: Usar el Ciclo Lectivo Oficial
            CicloLectivo cicloLectivo = cicloLectivoRepository.findByAnio(anioFiltro)
                    .orElseThrow(() -> new ReporteDesempenoDocenteException("No se encontró un Ciclo Lectivo para el año: " + anioFiltro));

            fechaInicio = cicloLectivo.getFechaDesde();
            fechaFin = cicloLectivo.getFechaHasta();

        } else if (filtros.getFechaDesde() != null && filtros.getFechaHasta() != null) {
            // Prioridad 2: Usar el Rango Personalizado
            fechaInicio = filtros.getFechaDesde();
            fechaFin = filtros.getFechaHasta();

            if (fechaInicio.isAfter(fechaFin)) {
                throw new ReporteDesempenoDocenteException("La fecha de inicio no puede ser posterior a la fecha de fin.");
            }
        } else {
            // Error: No se proporcionó ningún filtro de tiempo
            throw new ReporteDesempenoDocenteException("Debe proporcionar un año de ciclo lectivo o un rango de fechas (Desde/Hasta).");
        }

        //  OBTENER LISTA BASE DE DOCENTES A EVALUAR
        List<Docente> docentes = (filtros.getDocenteId() != null)
                ? docenteRepository.findAllById(List.of(filtros.getDocenteId()))
                : docenteRepository.findAll();

        if (docentes.isEmpty()) {
            throw new ReporteDesempenoDocenteException("No se encontraron docentes para los filtros aplicados.");
        }

        List<ReporteDesempenoDocenteDto> reporteFinal = new ArrayList<>();

    // PROCESAR CADA DOCENTE Y CONSTRUIR LAS FILAS (DTO)
        for (Docente docente : docentes) {

            // CÁLCULO DE TASA DE INASISTENCIAS
            Long totalInasistencias = asistenciaDocenteRepository.countInasistencias(
                    docente.getId(), fechaInicio, fechaFin);
            Long totalDiasRegistrados = asistenciaDocenteRepository.countTotalDiasRegistrados(
                    docente.getId(), fechaInicio, fechaFin);
            // Tasa = (Inasistencias / Días que DEBIÓ asistir) * 100
            double tasaInasistencias = (totalDiasRegistrados > 0)
                    ? (double) totalInasistencias / totalDiasRegistrados * 100.0
                    : 0.0;

            //  OBTENER DICTADOS DEL DOCENTE (Fila de la tabla)
            List<MateriaCurso> dictados = docente.getDictados();

            // Aplicar filtros de materia y curso si existen
            List<MateriaCurso> dictadosFiltrados = dictados.stream()
                    .filter(mc -> filtros.getMateriaId() == null || mc.getMateria().getId().equals(filtros.getMateriaId()))
                    .filter(mc -> filtros.getCursoId() == null || mc.getCurso().getId().equals(filtros.getCursoId()))
                    .collect(Collectors.toList());


            List<DetalleDictadoDto> detalles = new ArrayList<>();

            for (MateriaCurso dictado : dictadosFiltrados) {

                //  CÁLCULO DE PROMEDIO Y APROBACIÓN POR CADA DICTADO
                Long materiaCursoId = dictado.getId();

                Double promedio = calificacionRepository.calculateFinalGradeAverageByMateriaCurso(materiaCursoId);
                Double tasaAprobacion = calificacionRepository.calculateApprovalRateByMateriaCurso(materiaCursoId);

                detalles.add(DetalleDictadoDto.builder()
                        .materiaCursoNombre(dictado.getMateria().getNombre() + " - " + dictado.getCurso().getAnio() + " " + dictado.getCurso().getDivision())
                        .promedioAlumnos(promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0) // Redondear a un decimal
                        .tasaAprobacion(tasaAprobacion != null ? tasaAprobacion : 0.0)
                        .build());
            }
            //  CONSTRUIR DTO DE LA FILA
            reporteFinal.add(ReporteDesempenoDocenteDto.builder()
                    .docenteNombreCompleto(docente.getNombre() + " " + docente.getApellido())
                    .tasaInasistencias(Math.round(tasaInasistencias * 100.0) / 100.0) // Redondear a dos decimales
                    .detallesDictado(detalles)
                    .build());
        }

        //CONSTRUIR EL DTO DE FILTROS

        // Traducción de IDs a Nombres para el Front-end
        String docenteNombre = (filtros.getDocenteId() != null)
                ? docenteRepository.findById(filtros.getDocenteId())
                .map(d -> d.getNombre() + " " + d.getApellido()).orElse("-Docente no encontrado-")
                : "-Todos-";

        String materiaNombre = (filtros.getMateriaId() != null)
                ? materiaCursoRepository.findMateriaNombreById(filtros.getMateriaId()).orElse("-Materia no encontrada-")
                : "-Todos-";

        String cursoNombre = (filtros.getCursoId() != null)
                ? materiaCursoRepository.findCursoNombreById(filtros.getCursoId()).orElse("-Curso no encontrado-")
                : "-Todos-";

        FiltrosReporteDto filtrosFinales = FiltrosReporteDto.builder()
                // Identificadores usados
                .docenteId(filtros.getDocenteId())
                .materiaId(filtros.getMateriaId())
                .cursoId(filtros.getCursoId())
                .cicloLectivoAnioFiltro(anioFiltro) // Es null si no se usó ciclo lectivo

                // Nombres Descriptivos
                .docenteNombreCompleto(docenteNombre)
                .materiaNombre(materiaNombre)
                .cursoNombre(cursoNombre)

                // Rango de Fechas usado por el motor
                .rangoFechaDesde(fechaInicio)
                .rangoFechaHasta(fechaFin)
                .build();

        // ====================================================================
        // 5. CONSTRUIR Y RETORNAR EL DTO DE RESPUESTA FINAL
        // ====================================================================

        return ReporteDesempenoDocenteResponse.builder()
                .filtros(filtrosFinales)
                .filas(reporteFinal)
                .code(200) // Generalmente un 200 OK
                .mensaje("Reporte generado con éxito")
                .build();

    }

}
