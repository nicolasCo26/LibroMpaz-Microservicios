package com.librompaz.calificaciones.service;

import com.librompaz.calificaciones.dto.EvaluacionRequest;
import com.librompaz.calificaciones.dto.GuardarNotasRequest;
import com.librompaz.calificaciones.exception.ConflictoException;
import com.librompaz.calificaciones.exception.RecursoNoEncontradoException;
import com.librompaz.calificaciones.model.Calificacion;
import com.librompaz.calificaciones.model.Evaluacion;
import com.librompaz.calificaciones.repository.CalificacionRepository;
import com.librompaz.calificaciones.repository.EvaluacionRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lógica de negocio: el controlador solo recibe y responde; aquí se decide qué se guarda. */
@Service
public class CalificacionService {

    private final EvaluacionRepository evaluaciones;
    private final CalificacionRepository calificaciones;

    public CalificacionService(EvaluacionRepository evaluaciones, CalificacionRepository calificaciones) {
        this.evaluaciones = evaluaciones;
        this.calificaciones = calificaciones;
    }

    /** Todas las evaluaciones, o solo las de un curso y asignatura si vienen ambos filtros. */
    public List<Evaluacion> listarEvaluaciones(String cursoId, String asignaturaId) {
        if (cursoId != null && asignaturaId != null) {
            return evaluaciones.findByCursoIdAndAsignaturaIdOrderByFechaDesc(cursoId, asignaturaId);
        }
        return evaluaciones.findAllByOrderByFechaDesc();
    }

    public Evaluacion obtenerEvaluacion(String id) {
        return evaluaciones.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la evaluación " + id));
    }

    public Evaluacion crearEvaluacion(EvaluacionRequest datos) {
        String id = (datos.id() == null || datos.id().isBlank()) ? UUID.randomUUID().toString() : datos.id();
        if (evaluaciones.existsById(id)) {
            throw new ConflictoException("Ya existe una evaluación con id " + id);
        }
        return evaluaciones.save(new Evaluacion(
                id, datos.cursoId(), datos.asignaturaId(), datos.nombreInstrumento().trim(), datos.fecha()));
    }

    public Evaluacion actualizarEvaluacion(String id, EvaluacionRequest datos) {
        Evaluacion evaluacion = obtenerEvaluacion(id);
        evaluacion.setCursoId(datos.cursoId());
        evaluacion.setAsignaturaId(datos.asignaturaId());
        evaluacion.setNombreInstrumento(datos.nombreInstrumento().trim());
        evaluacion.setFecha(datos.fecha());
        return evaluaciones.save(evaluacion);
    }

    /** Borra la evaluación y todas sus notas en una sola transacción. */
    @Transactional
    public void eliminarEvaluacion(String id) {
        Evaluacion evaluacion = obtenerEvaluacion(id);
        calificaciones.eliminarDeEvaluacion(id);
        evaluaciones.delete(evaluacion);
    }

    public List<Calificacion> notasDe(String evaluacionId) {
        obtenerEvaluacion(evaluacionId);   // 404 si la evaluación no existe
        return calificaciones.findByEvaluacionId(evaluacionId);
    }

    /** Reemplaza la planilla completa: borra las notas anteriores e inserta las recibidas. */
    @Transactional
    public List<Calificacion> guardarNotas(String evaluacionId, GuardarNotasRequest datos) {
        obtenerEvaluacion(evaluacionId);
        calificaciones.eliminarDeEvaluacion(evaluacionId);
        List<Calificacion> nuevas = datos.notas().stream()
                .map(n -> new Calificacion(evaluacionId, n.estudianteId(), n.nota(), datos.registradoPor()))
                .toList();
        return calificaciones.saveAll(nuevas);
    }

    public List<Calificacion> listarCalificaciones() {
        return calificaciones.findAll();
    }
}
