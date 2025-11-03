package com.grup14.luterano.examples;

/**
 * REPORTE DE EXÁMENES CONSECUTIVOS DESAPROBADOS
 * ==============================================
 * 
 * Este reporte identifica alumnos que han desaprobado dos exámenes consecutivos
 * de la misma materia, considerando la estructura académica de 4 notas por etapa
 * y 2 etapas por año.
 * 
 * LÓGICA DE DETECCIÓN:
 * -------------------
 * 
 * 1. CASOS DETECTADOS:
 *    - Dos notas consecutivas en la misma etapa (ej: 2da y 3ra nota de Etapa 1)
 *    - Última nota de Etapa 1 y primera nota de Etapa 2 (4ta nota Etapa 1 + 1ra nota Etapa 2)
 * 
 * 2. CRITERIO DE DESAPROBACIÓN:
 *    - Nota < 7 se considera desaprobada
 * 
 * 3. VALIDACIONES DE ESTADO ACADÉMICO:
 *    - Solo alumnos que están CURSANDO actualmente (fechaHasta IS NULL en HistorialCurso)
 *    - Solo materias en estado: CURSANDO, EXAMEN, COLOQUIO, PENDIENTE_EXAMEN
 *    - Excluye alumnos dados de baja, trasladados o inactivos
 *    - Filtra por ciclo lectivo específico
 * 
 * 4. VALIDACIONES TEMPORALES:
 *    - Si solo hay 2 notas en Etapa 1 y ninguna en Etapa 2 → Analiza esas 2 notas
 *    - Si el año está en transcurso → Solo considera notas existentes
 *    - Agrupa por alumno y materia para análisis consecutivo
 * 
 * NIVELES DE RIESGO:
 * -----------------
 * - CRÍTICO: Promedio de ambas notas ≤ 4.0
 * - ALTO: Promedio de ambas notas ≤ 5.0  
 * - MEDIO: Promedio de ambas notas entre 5.1 y 6.9
 * 
 * ENDPOINTS DISPONIBLES:
 * =====================
 * 
 * 1. REPORTE INSTITUCIONAL COMPLETO:
 *    GET /api/reportes/examenes-consecutivos/institucional/{cicloLectivoAnio}
 *    
 *    Ejemplo: GET /api/reportes/examenes-consecutivos/institucional/2024
 *    
 *    Roles permitidos: ADMIN, DIRECTOR, PRECEPTOR
 *    
 *    Respuesta ejemplo:
 *    {
 *      "cicloLectivoAnio": 2024,
 *      "nombreCicloLectivo": "Ciclo Lectivo 2024",
 *      "totalAlumnosEnRiesgo": 12,
 *      "totalMateriasAfectadas": 8,
 *      "totalCursosAfectados": 5,
 *      "casosDetectados": [
 *        {
 *          "alumnoId": 45,
 *          "alumnoNombre": "Juan Carlos",
 *          "alumnoApellido": "García",
 *          "nombreCompleto": "García, Juan Carlos",
 *          "materiaId": 3,
 *          "materiaNombre": "Matemática",
 *          "cursoId": 2,
 *          "cursoNombre": "1° PRIMARIO A",
 *          "anio": 1,
 *          "division": "A",
 *          "primeraNota": 4,
 *          "etapaPrimeraNota": 1,
 *          "numeroPrimeraNota": 3,
 *          "segundaNota": 5,
 *          "etapaSegundaNota": 1,
 *          "numeroSegundaNota": 4,
 *          "descripcionConsecutivo": "3º nota Etapa 1 y 4º nota Etapa 1",
 *          "estadoRiesgo": "ALTO"
 *        },
 *        {
 *          "alumnoId": 67,
 *          "alumnoNombre": "María Elena",
 *          "alumnoApellido": "López",
 *          "nombreCompleto": "López, María Elena",
 *          "materiaId": 5,
 *          "materiaNombre": "Lengua",
 *          "cursoId": 3,
 *          "cursoNombre": "2° PRIMARIO B",
 *          "anio": 2,
 *          "division": "B",
 *          "primeraNota": 6,
 *          "etapaPrimeraNota": 1,
 *          "numeroPrimeraNota": 4,
 *          "segundaNota": 5,
 *          "etapaSegundaNota": 2,
 *          "numeroSegundaNota": 1,
 *          "descripcionConsecutivo": "4º nota Etapa 1 y 1º nota Etapa 2",
 *          "estadoRiesgo": "MEDIO"
 *        }
 *      ],
 *      "casosCriticos": 3,
 *      "casosAltos": 4,
 *      "casosMedios": 5,
 *      "resumenPorMateria": [
 *        {
 *          "materiaId": 3,
 *          "materiaNombre": "Matemática",
 *          "totalCasos": 4,
 *          "casosCriticos": 1,
 *          "casosAltos": 2,
 *          "casosMedios": 1
 *        },
 *        {
 *          "materiaId": 5,
 *          "materiaNombre": "Lengua",
 *          "totalCasos": 3,
 *          "casosCriticos": 0,
 *          "casosAltos": 1,
 *          "casosMedios": 2
 *        }
 *      ],
 *      "recomendaciones": [
 *        "URGENTE: 3 casos críticos requieren intervención inmediata (promedio ≤ 4)",
 *        "ATENCIÓN: 4 casos de alto riesgo necesitan seguimiento cercano",
 *        "PREVENCIÓN: 5 casos de riesgo medio requieren monitoreo",
 *        "La materia 'Matemática' presenta la mayor cantidad de casos (4) - Revisar metodología de evaluación",
 *        "Considerar reuniones con padres/tutores de alumnos afectados",
 *        "Evaluar estrategias de recuperación antes del cierre de etapa"
 *      ],
 *      "code": 0,
 *      "mensaje": "Reporte generado exitosamente"
 *    }
 * 
 * 2. REPORTE POR MATERIA ESPECÍFICA:
 *    GET /api/reportes/examenes-consecutivos/materia/{materiaId}/{cicloLectivoAnio}
 *    
 *    Ejemplo: GET /api/reportes/examenes-consecutivos/materia/3/2024
 *    
 *    Roles permitidos: ADMIN, DIRECTOR, PRECEPTOR, DOCENTE
 * 
 * 3. REPORTE POR CURSO ESPECÍFICO:
 *    GET /api/reportes/examenes-consecutivos/curso/{cursoId}/{cicloLectivoAnio}
 *    
 *    Ejemplo: GET /api/reportes/examenes-consecutivos/curso/2/2024
 *    
 *    Roles permitidos: ADMIN, DIRECTOR, PRECEPTOR
 * 
 * 4. RESUMEN EJECUTIVO:
 *    GET /api/reportes/examenes-consecutivos/resumen/{cicloLectivoAnio}
 *    
 *    Ejemplo: GET /api/reportes/examenes-consecutivos/resumen/2024
 *    
 *    Roles permitidos: ADMIN, DIRECTOR
 * 
 * CASOS DE USO PRÁCTICOS:
 * ======================
 * 
 * 1. DETECCIÓN TEMPRANA:
 *    - Identificar alumnos en riesgo antes del final del período
 *    - Implementar planes de apoyo específicos
 *    - Alertas para reuniones con padres
 * 
 * 2. ANÁLISIS PEDAGÓGICO:
 *    - Evaluar efectividad de metodologías de enseñanza
 *    - Identificar materias problemáticas
 *    - Comparar rendimiento entre cursos paralelos
 * 
 * 3. GESTIÓN INSTITUCIONAL:
 *    - Reportes para dirección y supervisión
 *    - Seguimiento de intervenciones aplicadas
 *    - Métricas de calidad educativa
 * 
 * 4. INTERVENCIÓN PERSONALIZADA:
 *    - Casos críticos: Apoyo académico intensivo inmediato
 *    - Casos altos: Plan de seguimiento semanal
 *    - Casos medios: Monitoreo y apoyo preventivo
 * 
 * CONFIGURACIÓN POSTMAN:
 * =====================
 * 
 * Headers requeridos:
 * - Authorization: Bearer {jwt_token}
 * - Content-Type: application/json
 * 
 * Variables de entorno sugeridas:
 * - base_url: http://localhost:8080
 * - jwt_token: {token_obtenido_en_login}
 * 
 * Tests recomendados para Postman:
 * 
 * pm.test("Status code is 200", function () {
 *     pm.response.to.have.status(200);
 * });
 * 
 * pm.test("Response has required fields", function () {
 *     var jsonData = pm.response.json();
 *     pm.expect(jsonData).to.have.property('cicloLectivoAnio');
 *     pm.expect(jsonData).to.have.property('casosDetectados');
 *     pm.expect(jsonData).to.have.property('totalAlumnosEnRiesgo');
 * });
 * 
 * pm.test("Code is success", function () {
 *     var jsonData = pm.response.json();
 *     pm.expect(jsonData.code).to.eql(0);
 * });
 */
public class EjemploReporteExamenesConsecutivos {
    // Esta clase sirve como documentación y ejemplos de uso
    // No contiene código ejecutable, solo documentación
}