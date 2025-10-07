package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.DiaSemana;
import com.grup14.luterano.exeptions.HorarioClaseModuloException;
import com.grup14.luterano.mappers.HorarioClaseModuloMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.horarioClaseModulo.SlotHorarioRequest;
import com.grup14.luterano.response.horarioClaseModulo.HorarioClaseModuloResponse;
import com.grup14.luterano.service.HorarioClaseModuloService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service@AllArgsConstructor
public class HorarioClaseModuloServiceImpl implements HorarioClaseModuloService {

    private final ModuloRepository moduloRepository;
    private final HorarioClaseModuloRepository horarioRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final MateriaRepository materiaRepository;
    private final CursoRepository cursoRepository;

    @Override
    @Transactional
    public HorarioClaseModuloResponse asignarHorariosAMateriaDeCurso(
            Long cursoId, Long materiaId, List<SlotHorarioRequest> slots) {

        if (cursoId == null || materiaId == null) {
            throw new HorarioClaseModuloException("Debe indicar cursoId y materiaId.");
        }
        if (slots == null || slots.isEmpty()) {
            throw new HorarioClaseModuloException("Debe indicar al menos un slot (día + moduloId).");
        }
        Materia materia= materiaRepository.findById(materiaId).orElseThrow(()->
                new HorarioClaseModuloException("No existe la materia con id "+materiaId));

        Curso curso = cursoRepository.findById(cursoId).orElseThrow(()->
                new HorarioClaseModuloException("No existe el curso con id "+cursoId));

        MateriaCurso mc = materiaCursoRepository.findByMateriaIdAndCursoId(materiaId, cursoId)
                .orElseThrow(() -> new HorarioClaseModuloException(
                        "La materia (id=" + materiaId + ") no está asignada al curso (id=" + cursoId + ")."));

        List<String> ok = new ArrayList<>();
        List<String> conflictos = new ArrayList<>();
        Set<String> dedupe = new HashSet<>();
        List<HorarioClaseModulo> creados = new ArrayList<>();

        for (SlotHorarioRequest s : slots) {
            if (s.getDia() == null || s.getModuloId() == null) {
                conflictos.add("Slot inválido (día y moduloId son obligatorios).");
                continue;
            }

            Modulo modulo = moduloRepository.findById(s.getModuloId())
                    .orElseThrow(() -> new HorarioClaseModuloException(
                            "Módulo no encontrado (id=" + s.getModuloId() + ")."));

            String key = s.getDia().name() + "#" + modulo.getId();
            if (!dedupe.add(key)) continue;


            boolean ocupadoCurso = horarioRepository
                    .existsByMateriaCurso_Curso_IdAndDiaSemanaAndModulo_Id(cursoId, s.getDia(), modulo.getId());
            if (ocupadoCurso) {
                conflictos.add(describe(s.getDia(), modulo) + " -> ya ocupado por otra materia del curso.");
                continue;
            }

            Docente docente = mc.getDocente();
            if (docente != null) {
                List<HorarioClaseModulo> choques = horarioRepository
                        .findConflictosDocente(docente.getId(), s.getDia(), modulo.getId());

                if (!choques.isEmpty()) {
                    for (HorarioClaseModulo h : choques) {
                        MateriaCurso mcChoque = h.getMateriaCurso();
                        Curso cursoChoque = mcChoque.getCurso();
                        String materiaNombre = mcChoque.getMateria().getNombre();

                        conflictos.add(
                                describe(s.getDia(), modulo) + " -> el docente ya dicta " + materiaNombre +
                                        " en " + prettyCurso(cursoChoque) + "."
                        );
                    }
                    continue;
                }
            }


            HorarioClaseModulo h = HorarioClaseModulo.builder()
                    .materiaCurso(mc)
                    .diaSemana(s.getDia())
                    .modulo(modulo)
                    .build();
            h = horarioRepository.save(h);

            creados.add(h);
            ok.add(describe(s.getDia(), modulo));
        }

        var dtoEjemplo = creados.isEmpty() ? null : HorarioClaseModuloMapper.toDto(creados.getFirst());

        String mensaje = conflictos.isEmpty()
                ? "Horarios asignados correctamente."
                : (ok.isEmpty()
                ? "No se asignó ningún horario por conflictos."
                : "Se asignaron algunos horarios; otros presentaron conflictos.");

        return HorarioClaseModuloResponse.builder()
                .code(0)
                .mensaje(mensaje)
                .horarioClaseModuloDto(dtoEjemplo)
                .slotsModificados(ok)
                .slotsConConflicto(conflictos)
                .build();
    }

    private String prettyCurso(Curso c) {
        return String.format("%s %d %s", c.getNivel(), c.getAnio(), c.getDivision());
    }

    @Override
    @Transactional
    public HorarioClaseModuloResponse desasignarHorariosAMateriaDeCurso(
            Long cursoId, Long materiaId, List<SlotHorarioRequest> slots) {

        if (cursoId == null || materiaId == null) {
            throw new HorarioClaseModuloException("Debe indicar cursoId y materiaId.");
        }
        if (slots == null || slots.isEmpty()) {
            throw new HorarioClaseModuloException("Debe indicar al menos un slot (día + moduloId).");
        }

        materiaRepository.findById(materiaId)
                .orElseThrow(() -> new HorarioClaseModuloException("No existe la materia con id " + materiaId));
        cursoRepository.findById(cursoId)
                .orElseThrow(() -> new HorarioClaseModuloException("No existe el curso con id " + cursoId));

        MateriaCurso mc = materiaCursoRepository.findByMateriaIdAndCursoId(materiaId, cursoId)
                .orElseThrow(() -> new HorarioClaseModuloException(
                        "La materia (id=" + materiaId + ") no está asignada al curso (id=" + cursoId + ")."));

        List<String> eliminados = new ArrayList<>();
        List<String> conflictos = new ArrayList<>();
        Set<String> dedupe = new HashSet<>();

        for (SlotHorarioRequest s : slots) {
            if (s.getDia() == null || s.getModuloId() == null) {
                conflictos.add("Slot inválido (día y moduloId son obligatorios).");
                continue;
            }

            Modulo modulo = moduloRepository.findById(s.getModuloId())
                    .orElseThrow(() -> new HorarioClaseModuloException(
                            "Módulo no encontrado (id=" + s.getModuloId() + ")."));

            String key = s.getDia().name() + "#" + modulo.getId();
            if (!dedupe.add(key)) continue;


            int count = horarioRepository.deleteByMateriaCurso_IdAndDiaSemanaAndModulo_Id(
                    mc.getId(), s.getDia(), modulo.getId());

            if (count > 0) {
                eliminados.add(describe(s.getDia(), modulo)); // lo usamos como "eliminados"
            } else {
                conflictos.add(describe(s.getDia(), modulo) + " -> no existe asignación para desasignar.");
            }
        }

        String mensaje = conflictos.isEmpty()
                ? "Horarios desasignados correctamente."
                : (eliminados.isEmpty()
                ? "No se desasignó ningún horario."
                : "Se desasignaron algunos horarios; otros no existían.");

        // No tenemos un bloque “representativo” luego de borrar: devolvemos null en el DTO principal
        return HorarioClaseModuloResponse.builder()
                .code(0)
                .mensaje(mensaje)
                .horarioClaseModuloDto(null)
                .slotsModificados(eliminados)
                .slotsConConflicto(conflictos)
                .build();
    }


    private String describe(DiaSemana dia, Modulo m) {
        return dia.name() + " - M" + m.getOrden() + " (" + m.getHoraDesde() + "-" + m.getHoraHasta() + ")";
    }

}
