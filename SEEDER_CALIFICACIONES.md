# Seeder de Calificaciones Aleatorias

Este seeder (`CalificacionSeeder`) genera calificaciones aleatorias para todos los alumnos en todas sus materias correspondientes.

## Funcionalidad

El seeder realiza las siguientes operaciones:

1. **Recorre todos los cursos** existentes en la base de datos
2. **Para cada curso**:
   - Obtiene todos los alumnos activos (excluye alumnos con estado EXCLUIDO)
   - Obtiene todas las materias asignadas al curso
3. **Para cada combinación alumno-materia**:
   - Verifica que exista un historial curso activo para el alumno
   - Verifica que exista un historial materia correspondiente
4. **Genera calificaciones**:
   - **2 etapas** (primera y segunda etapa del año)
   - **4 notas por etapa** (total 8 notas por materia por alumno)
   - **Notas aleatorias entre 1 y 10**
   - **Fechas aleatorias**:
     - Etapa 1: Marzo - Julio
     - Etapa 2: Agosto - Noviembre
5. **Validación**: Antes de crear cada calificación, verifica que no exista ya una calificación para esa etapa y número de nota

## Activación del Seeder

Para ejecutar el seeder, usa el siguiente comando:

```bash
mvn spring-boot:run -Dspring.profiles.active=seed-calificaciones
```

O si usas Java directamente:
```bash
java -jar target/luterano.jar --spring.profiles.active=seed-calificaciones
```

## Logs y Seguimiento

El seeder proporciona logs detallados:
- Información general del progreso por curso
- Advertencias para casos donde falten datos
- Resumen final con:
  - Total de calificaciones nuevas creadas
  - Total de calificaciones que ya existían

## Consideraciones

### Prerrequisitos
- Deben existir cursos en la base de datos
- Deben existir alumnos asignados a los cursos
- Deben existir materias asignadas a los cursos
- Deben existir registros de HistorialCurso activos (fechaHasta = NULL)
- Deben existir registros de HistorialMateria correspondientes

### Comportamiento Seguro
- **No sobrescribe** calificaciones existentes
- **Salta** alumnos sin historial curso activo
- **Salta** materias sin historial materia
- **Maneja errores** de forma graceful sin detener el proceso

### Estructura de Datos Generada
Para cada alumno en cada materia se crean:
- Etapa 1, Nota 1: Calificación aleatoria 1-10
- Etapa 1, Nota 2: Calificación aleatoria 1-10
- Etapa 1, Nota 3: Calificación aleatoria 1-10
- Etapa 1, Nota 4: Calificación aleatoria 1-10
- Etapa 2, Nota 1: Calificación aleatoria 1-10
- Etapa 2, Nota 2: Calificación aleatoria 1-10
- Etapa 2, Nota 3: Calificación aleatoria 1-10
- Etapa 2, Nota 4: Calificación aleatoria 1-10

## Ejemplo de Uso

```bash
# 1. Activar el seeder
mvn spring-boot:run -Dspring.profiles.active=seed-calificaciones

# 2. Verificar en logs el progreso
# El seeder mostrará información como:
# ==> Iniciando siembra de calificaciones aleatorias para todos los cursos...
# Encontrados 12 cursos para procesar
# Procesando curso: 1 PRIMARIA División A
# Encontrados 30 alumnos activos en el curso
# Encontradas 8 materias para el curso
# ...
# ==> Siembra de calificaciones finalizada.
# Total de calificaciones nuevas creadas: 2880
# Total de calificaciones que ya existían: 0
```

## Integración con Otros Componentes

Este seeder utiliza:
- `CalificacionService.crearCalificacion()` para crear las calificaciones
- Validaciones existentes del sistema
- Repositorios estándar para consultas
- Estructura de datos existente (HistorialCurso, HistorialMateria, etc.)

Por tanto, respeta todas las reglas de negocio y validaciones implementadas en el sistema.