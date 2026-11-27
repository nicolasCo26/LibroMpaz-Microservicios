package com.librompaz.anotaciones.controller;

import com.librompaz.anotaciones.dto.AnotacionRequest;
import com.librompaz.anotaciones.model.Anotacion;
import com.librompaz.anotaciones.model.TipoAnotacion;
import com.librompaz.anotaciones.service.AnotacionService;
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

/** Endpoints REST de la hoja de vida. */
@RestController
@RequestMapping("/api/anotaciones")
public class AnotacionController {

    private final AnotacionService servicio;

    public AnotacionController(AnotacionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Anotacion> listar(@RequestParam(required = false) String estudianteId,
                                  @RequestParam(required = false) TipoAnotacion tipo) {
        return servicio.listar(estudianteId, tipo);
    }

    @GetMapping("/{id}")
    public Anotacion obtener(@PathVariable String id) {
        return servicio.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Anotacion> crear(@Valid @RequestBody AnotacionRequest datos) {
        Anotacion creada = servicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/anotaciones/" + creada.getId())).body(creada);
    }

    @PutMapping("/{id}")
    public Anotacion actualizar(@PathVariable String id, @Valid @RequestBody AnotacionRequest datos) {
        return servicio.actualizar(id, datos);
    }

    /** Ejemplo: DELETE /api/anotaciones/AN1?solicitante=Docente de Matemática */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id, @RequestParam String solicitante) {
        servicio.eliminar(id, solicitante);
        return ResponseEntity.noContent().build();
    }
}
