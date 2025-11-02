package com.grup14.luterano.examples;

/**
 * EJEMPLO DE INTEGRACIÓN DEL SERVICIO NotaFinalService
 * 
 * Este archivo muestra cómo integrar el nuevo servicio NotaFinalService
 * en otros servicios existentes para reemplazar la lógica de nota final.
 */

// EJEMPLO 1: Integración en ReporteAnualServiceImpl
//
// En lugar de hacer esto en línea 183 de ReporteAnualServiceImpl:
// .notaFinal(mea != null ? mea.getNotaFinal() : null)
//
// Podés hacer esto:
/*
@Service
@RequiredArgsConstructor
public class ReporteAnualServiceImpl implements ReporteAnualService {
    
    private final NotaFinalService notaFinalService; // Agregar esta dependencia
    // ... otros campos
    
    private ReporteAnualAlumnoResponse generarReporteAnual(Alumno alumno, int anio) {
        // ... código existente hasta la línea donde se construye MateriaAnualDetalleDto
        
        materias.put(mId, MateriaAnualDetalleDto.builder()
                .materiaId(mId)
                .materiaNombre(r.getMateriaNombre())
                .e1Notas(r.getE1Notas())
                .e2Notas(r.getE2Notas())
                .e1(r.getE1())
                .e2(r.getE2())
                .pg(r.getPg())
                .estado(r.getEstado())
                .notaFinal(notaFinalService.calcularNotaFinal(alumno.getId(), mId, anio)) // ← USAR EL NUEVO SERVICIO
                .estadoFinal(estadoFinal)
                .estadoMateria(estadoMateria)
                .build());
    }
}
*/

// EJEMPLO 2: Integración en el cálculo de promedio de curso
//
// En lugar de hacer esto en línea 285 de ReporteAnualServiceImpl:
// Integer nf = (mea != null) ? mea.getNotaFinal() : null;
// if (nf != null) { suma += nf; n++; continue; }
// if (r.getPg() != null) { suma += r.getPg(); n++; }
//
// Podés hacer esto:
/*
private BigDecimal calcularPromedioCursoDesdeMaterias(Map<Long, MesaExamenAlumno> finales,
                                                      Map<Long, CalificacionesMateriaResumenDto> porMateria,
                                                      Long alumnoId, int anio) {
    if (porMateria == null || porMateria.isEmpty()) return null;
    double suma = 0.0; int n = 0;
    
    for (var entry : porMateria.entrySet()) {
        Long mId = entry.getKey();
        
        // Usar el nuevo servicio para obtener la nota final
        Integer notaFinal = notaFinalService.calcularNotaFinal(alumnoId, mId, anio);
        if (notaFinal != null) {
            suma += notaFinal;
            n++;
        }
    }
    
    if (n == 0) return null;
    double promedio = Math.round((suma / n) * 10.0) / 10.0;
    return BigDecimal.valueOf(promedio).setScale(1, RoundingMode.HALF_UP);
}
*/

// EJEMPLO 3: Uso en nuevos servicios
/*
@Service
@RequiredArgsConstructor
public class MiNuevoServicio {
    
    private final NotaFinalService notaFinalService;
    
    public void calcularAlgo(Long alumnoId, Long materiaId, int anio) {
        // Obtener solo la nota final (sin detalles)
        Integer notaFinal = notaFinalService.calcularNotaFinal(alumnoId, materiaId, anio);
        
        // O obtener detalles completos
        NotaFinalDetalleDto detalle = notaFinalService.obtenerNotaFinalDetallada(alumnoId, materiaId, anio);
        
        if (detalle != null) {
            System.out.println("Nota final: " + detalle.getNotaFinal());
            System.out.println("Origen: " + detalle.getOrigen());
            
            if ("MESA_EXAMEN".equals(detalle.getOrigen())) {
                System.out.println("Viene de mesa de examen ID: " + detalle.getMesaExamenId());
            } else if ("PROMEDIO_GENERAL".equals(detalle.getOrigen())) {
                System.out.println("Viene de PG truncado: " + detalle.getPromedioGeneral() + " → " + detalle.getNotaFinal());
            }
        }
    }
}
*/

public class EjemploIntegracionNotaFinal {
    
    /*
     * LÓGICA IMPLEMENTADA:
     * 
     * 1. Si el alumno rindió mesa de examen en el año:
     *    ✅ Usar notaFinal de la mesa más reciente
     * 
     * 2. Si NO rindió mesa de examen:
     *    ✅ Calcular PG desde calificaciones E1/E2
     *    ✅ Truncar el PG (Math.floor) para quitar decimales
     *    ✅ Ejemplo: PG = 7.8 → Nota Final = 7
     * 
     * ENDPOINTS DISPONIBLES:
     * 
     * GET /notas-finales/alumno/{alumnoId}/materia/{materiaId}?anio=2025
     * → Devuelve nota final con detalles del origen
     * 
     * GET /notas-finales/simple/alumno/{alumnoId}/materia/{materiaId}?anio=2025  
     * → Devuelve solo el número de la nota final
     */
}