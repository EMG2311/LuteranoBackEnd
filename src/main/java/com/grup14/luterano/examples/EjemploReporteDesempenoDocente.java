package com.grup14.luterano.examples;

/**
 * REPORTE DE DESEMPEÑO DOCENTE - Guía de Uso
 * <p>
 * Este reporte analiza las tasas de aprobación/reprobación por docente y materia,
 * permitiendo comparar el rendimiento entre docentes e identificar patrones.
 * <p>
 * =============================================================================
 * ENDPOINTS DISPONIBLES:
 * =============================================================================
 * <p>
 * 1. REPORTE COMPLETO POR CICLO LECTIVO
 * GET /reportes/desempeno-docente/{cicloLectivoAnio}
 * <p>
 * Ejemplo: GET /reportes/desempeno-docente/2024
 * <p>
 * Retorna análisis completo de todas las materias y docentes del año.
 * <p>
 * 2. REPORTE POR MATERIA ESPECÍFICA
 * GET /reportes/desempeno-docente/{cicloLectivoAnio}/materia/{materiaId}
 * <p>
 * Ejemplo: GET /reportes/desempeno-docente/2024/materia/1
 * <p>
 * Compara todos los docentes que dictan Matemática en 2024.
 * <p>
 * 3. REPORTE POR DOCENTE ESPECÍFICO
 * GET /reportes/desempeno-docente/{cicloLectivoAnio}/docente/{docenteId}
 * <p>
 * Ejemplo: GET /reportes/desempeno-docente/2024/docente/5
 * <p>
 * Analiza todas las materias que dictó Josefina en 2024.
 * <p>
 * =============================================================================
 * ESTRUCTURA DE LA RESPUESTA:
 * =============================================================================
 * <p>
 * {
 * "code": 0,
 * "mensaje": "Reporte generado exitosamente",
 * "cicloLectivoAnio": 2024,
 * "nombreCicloLectivo": "Ciclo Lectivo 2024",
 * "totalMaterias": 8,
 * "totalDocentes": 12,
 * "totalAlumnos": 340,
 * "totalCursos": 6,
 * "resultadosPorMateria": [
 * {
 * "materiaId": 1,
 * "nombreMateria": "Matemática",
 * "totalDocentes": 3,
 * "totalAlumnos": 85,
 * "promedioAprobacionMateria": 72.50,
 * "promedioReprobacionMateria": 27.50,
 * "rangoAprobacion": 45.00,
 * "resultadosPorDocente": [
 * {
 * "docenteId": 3,
 * "apellidoDocente": "García",
 * "nombreDocente": "Josefina",
 * "nombreCompletoDocente": "García, Josefina",
 * "materiaId": 1,
 * "nombreMateria": "Matemática",
 * "cursoCompleto": "1° A (BASICO)",
 * "totalAlumnos": 28,
 * "alumnosAprobados": 26,
 * "alumnosDesaprobados": 2,
 * "porcentajeAprobacion": 92.86,
 * "porcentajeReprobacion": 7.14,
 * "promedioGeneral": 7.85,
 * "notaMinima": 4.00,
 * "notaMaxima": 10.00,
 * "estadoAnalisis": "EXCELENTE"
 * },
 * {
 * "docenteId": 7,
 * "apellidoDocente": "Fernández",
 * "nombreDocente": "Raúl",
 * "nombreCompletoDocente": "Fernández, Raúl",
 * "materiaId": 1,
 * "nombreMateria": "Matemática",
 * "cursoCompleto": "1° B (BASICO)",
 * "totalAlumnos": 30,
 * "alumnosAprobados": 15,
 * "alumnosDesaprobados": 15,
 * "porcentajeAprobacion": 50.00,
 * "porcentajeReprobacion": 50.00,
 * "promedioGeneral": 5.90,
 * "notaMinima": 2.00,
 * "notaMaxima": 9.00,
 * "estadoAnalisis": "PREOCUPANTE"
 * }
 * ],
 * "mejorDocente": { "nombreCompletoDocente": "García, Josefina", "porcentajeAprobacion": 92.86 },
 * "peorDocente": { "nombreCompletoDocente": "Fernández, Raúl", "porcentajeAprobacion": 50.00 }
 * }
 * ],
 * "resumenEjecutivo": "Promedio institucional de aprobación: 74.20%. Se analizaron 8 materias con 12 docentes en total.",
 * "hallazgosImportantes": [
 * "La materia Matemática presenta la mayor variación entre docentes (42.86% de diferencia)",
 * "2 materia(s) tienen tasa de aprobación menor al 60%"
 * ],
 * "recomendaciones": [
 * "Realizar reuniones de intercambio de metodologías entre docentes de la misma materia",
 * "Implementar capacitaciones para docentes con tasas de aprobación menores al 70%",
 * "Analizar factores externos que puedan estar afectando el rendimiento estudiantil"
 * ]
 * }
 * <p>
 * =============================================================================
 * CASOS DE USO TÍPICOS:
 * =============================================================================
 * <p>
 * 1. COMPARACIÓN ENTRE DOCENTES DE LA MISMA MATERIA:
 * "¿Por qué Josefina tiene 92% de aprobación en Matemática y Raúl solo 50%?"
 * → Usar endpoint por materia específica
 * <p>
 * 2. EVALUACIÓN ANUAL DE DOCENTE:
 * "¿Cómo le fue a Josefina en todas sus materias este año?"
 * → Usar endpoint por docente específico
 * <p>
 * 3. ANÁLISIS INSTITUCIONAL:
 * "¿Qué materias tienen mayor tasa de reprobación en el colegio?"
 * → Usar endpoint completo por ciclo lectivo
 * <p>
 * 4. IDENTIFICACIÓN DE PROBLEMAS:
 * "¿Hay docentes que necesitan capacitación?"
 * → Buscar estados "PREOCUPANTE" en el reporte completo
 * <p>
 * =============================================================================
 * INTERPRETACIÓN DE RESULTADOS:
 * =============================================================================
 * <p>
 * ESTADOS DE ANÁLISIS:
 * • EXCELENTE:    ≥ 90% aprobación
 * • BUENO:        ≥ 75% aprobación
 * • REGULAR:      ≥ 60% aprobación
 * • PREOCUPANTE:  < 60% aprobación
 * <p>
 * INDICADORES CLAVE:
 * • rangoAprobacion: Diferencia entre mejor y peor docente de la materia
 * • promedioGeneral: Nota promedio de las calificaciones finales
 * • hallazgosImportantes: Alertas automáticas sobre patrones preocupantes
 * <p>
 * =============================================================================
 * PERMISOS REQUERIDOS:
 * =============================================================================
 * <p>
 * Roles autorizados: ADMIN, DIRECTOR, PRECEPTOR
 * <p>
 * =============================================================================
 * EJEMPLO DE POSTMAN:
 * =============================================================================
 * <p>
 * GET http://localhost:8080/reportes/desempeno-docente/2024
 * Headers:
 * Authorization: Bearer {tu_token_jwt}
 * Content-Type: application/json
 */
public class EjemploReporteDesempenoDocente {
    // Esta clase solo contiene documentación
    // Los endpoints están en ReporteDesempenoDocenteController
}