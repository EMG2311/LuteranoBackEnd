package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.notaFinal.NotaFinalDetalleDto;
import com.grup14.luterano.dto.reporteNotas.CalificacionesMateriaResumenDto;
import com.grup14.luterano.entities.MesaExamenAlumno;
import com.grup14.luterano.repository.MesaExamenAlumnoRepository;
import com.grup14.luterano.response.reporteNotas.CalificacionesAlumnoAnioResponse;
import com.grup14.luterano.service.NotaFinalService;
import com.grup14.luterano.service.ReporteNotasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotaFinalServiceImpl implements NotaFinalService {

    private final MesaExamenAlumnoRepository mesaExamenAlumnoRepo;
    private final ReporteNotasService reporteNotasService;

    @Override
    @Transactional(readOnly = true)
    public Integer calcularNotaFinal(Long alumnoId, Long materiaId, int anio) {
        NotaFinalDetalleDto detalle = obtenerNotaFinalDetallada(alumnoId, materiaId, anio);
        return detalle != null ? detalle.getNotaFinal() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public NotaFinalDetalleDto obtenerNotaFinalDetallada(Long alumnoId, Long materiaId, int anio) {
        // 1. Buscar mesa de examen más reciente en el año
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        List<MesaExamenAlumno> mesas = mesaExamenAlumnoRepo
                .findByAlumno_IdAndMesaExamen_FechaBetween(alumnoId, desde, hasta);

        // Filtrar por materia y obtener la más reciente
        MesaExamenAlumno mesaMasReciente = mesas.stream()
                .filter(mea -> mea.getMesaExamen().getMateriaCurso().getMateria().getId().equals(materiaId))
                .filter(mea -> mea.getNotaFinal() != null)
                .max((a, b) -> {
                    LocalDate fechaA = a.getMesaExamen().getFecha();
                    LocalDate fechaB = b.getMesaExamen().getFecha();
                    if (fechaA == null && fechaB == null) return 0;
                    if (fechaA == null) return -1;
                    if (fechaB == null) return 1;
                    return fechaA.compareTo(fechaB);
                })
                .orElse(null);

        // 2. Si tiene mesa de examen, usar esa nota
        if (mesaMasReciente != null) {
            return NotaFinalDetalleDto.desdeMesa(
                    mesaMasReciente.getNotaFinal(),
                    mesaMasReciente.getMesaExamen().getId()
            );
        }

        // 3. Si no tiene mesa, calcular desde PG truncado
        CalificacionesAlumnoAnioResponse reporteNotas = reporteNotasService.listarResumenPorAnio(alumnoId, anio);

        if (reporteNotas == null || reporteNotas.getCalificacionesAlumnoResumenDto() == null) {
            return null;
        }

        CalificacionesMateriaResumenDto materia = reporteNotas.getCalificacionesAlumnoResumenDto()
                .getMaterias().stream()
                .filter(m -> m.getMateriaId().equals(materiaId))
                .findFirst()
                .orElse(null);

        if (materia == null || materia.getPg() == null) {
            return null;
        }

        // Truncar el PG (quitar decimales)
        Integer notaFinalTruncada = (int) Math.floor(materia.getPg());

        return NotaFinalDetalleDto.desdePromedio(notaFinalTruncada, materia.getPg());
    }
}