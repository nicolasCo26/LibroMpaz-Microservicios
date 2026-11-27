package com.librompaz.calificaciones.controller;

import com.librompaz.calificaciones.dto.EvaluacionRequest;
import com.librompaz.calificaciones.dto.GuardarNotasRequest;
import com.librompaz.calificaciones.model.Calificacion;
import com.librompaz.calificaciones.model.Evaluacion;
import com.librompaz.calificaciones.service.CalificacionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints REST del microservicio. Todas las rutas parten con /api. */
@RestController
@RequestMapping("/api")
public class CalificacionController {

    private final CalificacionService servicio;

    public CalificacionController(CalificacionService servicio) {
        this.servicio = servicio;
    }

    // ---------- Evaluaciones ----------

    @GetMapping("/evaluaciones")
    public List<Evaluacion> listar(@RequestParam(required = false) String cursoId,
                                   @RequestParam(required = false) String asignaturaId) {
        return servicio.listarEvaluaciones(cursoId, asignaturaId);
    }

    @GetMapping("/evaluaciones/{id}")
    public Evaluacion obtener(@PathVariable String id) {
        return servicio.obtenerEvaluacion(id);
    }

    @PostMapping("/evaluaciones")
    public ResponseEntity<Evaluacion> crear(@Valid @RequestBody EvaluacionRequest datos) {
        Evaluacion creada = servicio.crearEvaluacion(datos);
        return ResponseEntity.created(URI.create("/api/evaluaciones/" + creada.getId())).body(creada);
    }

    @PutMapping("/evaluaciones/{id}")
    public Evaluacion actualizar(@PathVariable String id, @Valid @RequestBody EvaluacionRequest datos) {
        return servicio.actualizarEvaluacion(id, datos);
    }

    @DeleteMapping("/evaluaciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        servicio.eliminarEvaluacion(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Calificaciones (notas) ----------

    @GetMapping("/evaluaciones/{id}/calificaciones")
    public List<Calificacion> notasDe(@PathVariable String id) {
        return servicio.notasDe(id);
    }

    @PutMapping("/evaluaciones/{id}/calificaciones")
    public List<Calificacion> guardarNotas(@PathVariable String id, @Valid @RequestBody GuardarNotasRequest datos) {
        return servicio.guardarNotas(id, datos);
    }

    @GetMapping("/calificaciones")
    public List<Calificacion> todasLasNotas() {
        return servicio.listarCalificaciones();
    }
}
