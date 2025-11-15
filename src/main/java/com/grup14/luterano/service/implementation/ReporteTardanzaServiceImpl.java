package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteTardanza.TardanzaDetalleDto;
import com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto;
import com.grup14.luterano.exeptions.ReporteTardanzaException;
import com.grup14.luterano.repository.AsistenciaAlumnoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.response.reporteTardanza.ReporteTardanzasResponseList;
import com.grup14.luterano.service.ReporteTardanzaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteTardanzaServiceImpl implements ReporteTardanzaService {

    private final AsistenciaAlumnoRepository repo;
    private final CursoRepository cursoRepository;

    @Override
    @Transactional(readOnly = true)
    public ReporteTardanzasResponseList listarPorCurso(Long cursoId, LocalDate desde, LocalDate hasta, Integer limit) {
        if (cursoId == null) {
            return ReporteTardanzasResponseList.builder()
                    .items(List.of()).code(422).mensaje("Debe indicar cursoId").build();
        }
        cursoRepository.findById(cursoId).orElseThrow(() ->
                new ReporteTardanzaException("No existe el curso con id " + cursoId));

        List<TardanzaRowDto> items = (limit == null || limit <= 0)
                ? repo.tardanzasPorCurso(cursoId, desde, hasta)
                : repo.tardanzasPorCurso(cursoId, desde, hasta, PageRequest.of(0, limit));
        
        // Poblar los detalles de cada tardanza
        items.forEach(item -> item.setDetalles(obtenerDetallesTardanzas(item.getAlumnoId(), desde, hasta)));
        
        return ReporteTardanzasResponseList.builder().items(items).code(200).mensaje("OK").build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteTardanzasResponseList listarTodos(LocalDate desde, LocalDate hasta, Integer limit) {
        List<TardanzaRowDto> items = (limit == null || limit <= 0)
                ? repo.tardanzasTodosCursos(desde, hasta)
                : repo.tardanzasTodosCursos(desde, hasta, PageRequest.of(0, limit));
        
        // Poblar los detalles de cada tardanza
        items.forEach(item -> item.setDetalles(obtenerDetallesTardanzas(item.getAlumnoId(), desde, hasta)));
        
        return ReporteTardanzasResponseList.builder().items(items).code(200).mensaje("OK").build();
    }
    
    private List<TardanzaDetalleDto> obtenerDetallesTardanzas(Long alumnoId, LocalDate desde, LocalDate hasta) {
        return repo.findTardanzasPorAlumno(alumnoId, desde, hasta).stream()
                .map(asistencia -> TardanzaDetalleDto.builder()
                        .fecha(asistencia.getFecha())
                        .observacion(asistencia.getObservacion())
                        .build())
                .collect(Collectors.toList());
    }
}
