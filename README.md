# Libro MPAZ — Microservicios

Backend de la app [Libro MPAZ](https://github.com/nicolasCo26/LibroMpaz) (libro digital de clases de la Escuela Escritora Marcela Paz). Dos microservicios independientes en **Spring Boot 4 + Java 21**, cada uno con su propia base de datos **H2** en archivo.

| Microservicio | Puerto | Datos | Base |
|---|---|---|---|
| `ms-calificaciones` | 8081 | Evaluaciones (instrumentos) y notas de 1,0 a 7,0 | `ms-calificaciones/data/calificaciones.mv.db` |
| `ms-anotaciones` | 8082 | Hoja de vida: anotaciones positivas, negativas y observaciones | `ms-anotaciones/data/anotaciones.mv.db` |

## Integrantes

| Nombre | Usuario GitHub |
|---|---|
| Nicolás Navarrete Reyes | [nicolasCo26](https://github.com/nicolasCo26) |
| Camila Leiva | [alemi-vy](https://github.com/alemi-vy) |

## Cómo ejecutar

Requiere **JDK 21** (no hace falta instalar Maven: cada proyecto trae su Maven Wrapper). En dos terminales:

```bash
cd ms-calificaciones
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run

cd ms-anotaciones
./mvnw spring-boot:run
```

La primera vez cada servicio crea su base y carga los datos ficticios iniciales (`config/DatosIniciales.java`), con los mismos ids que usa la app.

- Probar: http://localhost:8081/api/evaluaciones · http://localhost:8082/api/anotaciones
- Ver la base: http://localhost:8081/h2-console y http://localhost:8082/h2-console (JDBC URL `jdbc:h2:file:./data/calificaciones` o `jdbc:h2:file:./data/anotaciones`, usuario `sa`, sin clave).
- Volver a los datos iniciales: detener el servicio y borrar su carpeta `data/`.
- Desde el emulador Android, el computador es `http://10.0.2.2:8081` y `http://10.0.2.2:8082`.

**Pruebas:** `./mvnw test` en cada carpeta (prueba unitaria del servicio con Mockito + prueba de integración de los endpoints con MockMvc y H2 en memoria).

## Endpoints

### ms-calificaciones (8081)

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/evaluaciones` (opcional `?cursoId=C3A&asignaturaId=MAT`) | 200 lista |
| GET | `/api/evaluaciones/{id}` | 200 · 404 si no existe |
| POST | `/api/evaluaciones` | 201 creada · 400 datos inválidos · 409 id repetido |
| PUT | `/api/evaluaciones/{id}` | 200 actualizada · 404 |
| DELETE | `/api/evaluaciones/{id}` | 204 (borra también sus notas) · 404 |
| GET | `/api/evaluaciones/{id}/calificaciones` | 200 notas de la evaluación |
| PUT | `/api/evaluaciones/{id}/calificaciones` | 200: reemplaza la planilla completa · 400 si una nota está fuera de 1,0–7,0 |
| GET | `/api/calificaciones` | 200 todas las notas |

```json
POST /api/evaluaciones
{ "id": "opcional-uuid", "cursoId": "C3A", "asignaturaId": "MAT", "nombreInstrumento": "Prueba unidad 2", "fecha": "2026-11-20" }

PUT /api/evaluaciones/EV1/calificaciones
{ "registradoPor": "Docente de Matemática", "notas": [ { "estudianteId": "E3A-1", "nota": 6.5 } ] }
```

### ms-anotaciones (8082)

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/anotaciones` (opcional `?estudianteId=E3A-2` o `?tipo=NEGATIVA`) | 200 lista |
| GET | `/api/anotaciones/{id}` | 200 · 404 |
| POST | `/api/anotaciones` | 201: el servidor pone `fechaHora` (hora de Chile) · 400 |
| PUT | `/api/anotaciones/{id}` | 200 corrige tipo y descripción · 403 si `firmadoPor` no es quien la firmó |
| DELETE | `/api/anotaciones/{id}?solicitante=Docente de Matemática` | 204 · 403 si no es quien la firmó · 404 |

```json
POST /api/anotaciones
{ "estudianteId": "E3A-1", "tipo": "POSITIVA", "descripcion": "Ayuda a sus compañeros.", "firmadoPor": "Docente de Matemática" }
```

## Estructura (igual en los dos)

```
src/main/java/com/librompaz/<servicio>/
├── model/        Entidades JPA (@Entity): las tablas
├── dto/          Lo que recibe cada endpoint, con validaciones (@NotBlank, @Size, @DecimalMin...)
├── repository/   Interfaces JpaRepository: Spring Data genera las consultas
├── service/      Lógica de negocio (reglas, transacciones, 404/403/409)
├── controller/   @RestController: rutas HTTP, solo recibe y responde
├── exception/    Excepciones propias y @RestControllerAdvice que las convierte en JSON con su código HTTP
└── config/       DatosIniciales: carga los datos ficticios si la base está vacía
```

**Reglas de negocio en el backend:**
- La nota debe estar entre 1,0 y 7,0 y el nombre del instrumento entre 3 y 60 caracteres (aunque la app ya lo valide, el servidor no confía en el cliente).
- Eliminar una evaluación borra sus notas en la misma transacción (`@Transactional`).
- La fecha y hora de una anotación la pone el servidor: el teléfono no puede cambiar la hora de la firma.
- Solo quien firmó una anotación puede corregirla o eliminarla (403 Forbidden).
