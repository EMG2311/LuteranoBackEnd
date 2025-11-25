package com.grup14.luterano.controller;

import com.grup14.luterano.service.ElegibilidadMesaExamenService;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.repository.MesaExamenRepository;
import com.grup14.luterano.entities.MesaExamen;
import com.grup14.luterano.entities.enums.CondicionRinde;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import com.grup14.luterano.response.mesaExamen.ElegibilidadMesaExamenResponse;

@RestController
@RequestMapping("/elegibilidad/mesa-examen")
@RequiredArgsConstructor
public class ElegibilidadMesaExamenController {
    private final ElegibilidadMesaExamenService elegibilidadMesaExamenService;
    private final MesaExamenRepository mesaExamenRepository;
    private final AlumnoRepository alumnoRepository;
    private final MateriaCursoRepository materiaCursoRepository;

    @GetMapping("/{mesaId}/alumnos")
    public ResponseEntity<ElegibilidadMesaExamenResponse> getElegiblesPorMesa(@PathVariable Long mesaId) {
        try {
            MesaExamen mesa = mesaExamenRepository.findById(mesaId).orElse(null);
            if (mesa == null) {
                return ResponseEntity.status(422).body(ElegibilidadMesaExamenResponse.builder()
                        .code(-1)
                        .mensaje("No se encontró la mesa de examen")
                        .alumnos(Collections.emptyList())
                        .build());
            }
            MateriaCurso materiaCurso = mesa.getMateriaCurso();
            LocalDate fechaMesa = mesa.getFecha();
            List<Alumno> alumnos = alumnoRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Alumno alumno : alumnos) {
                Optional<CondicionRinde> cond = elegibilidadMesaExamenService.determinarCondicionRinde(alumno, materiaCurso, fechaMesa);
                if (cond.isPresent()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("alumnoId", alumno.getId());
                    map.put("apellido", alumno.getApellido());
                    map.put("nombre", alumno.getNombre());
                    map.put("condicion", cond.get().name());
                    result.add(map);
                }
            }
            return ResponseEntity.ok(ElegibilidadMesaExamenResponse.builder()
                    .code(0)
                    .mensaje("OK")
                    .alumnos(result)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ElegibilidadMesaExamenResponse.builder()
                    .code(-2)
                    .mensaje("Error no controlado: " + e.getMessage())
                    .alumnos(Collections.emptyList())
                    .build());
        }
    }
}
