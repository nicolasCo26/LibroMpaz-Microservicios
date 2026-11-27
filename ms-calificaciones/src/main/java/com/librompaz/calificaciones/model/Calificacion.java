package com.librompaz.calificaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Nota de un estudiante en una evaluación (escala 1,0 a 7,0). Tabla "calificaciones".
 * Una sola nota por estudiante y evaluación: el id es "evaluacionId_estudianteId".
 */
@Entity
@Table(name = "calificaciones", indexes = @Index(columnList = "evaluacionId"))
public class Calificacion {

    @Id
    private String id;

    @Column(nullable = false)
    private String evaluacionId;

    @Column(nullable = false)
    private String estudianteId;

    @Column(nullable = false)
    private Double nota;

    /** Nombre del docente que registró la nota. */
    @Column(nullable = false)
    private String registradoPor;

    protected Calificacion() {
        // Requerido por JPA
    }

    public Calificacion(String evaluacionId, String estudianteId, Double nota, String registradoPor) {
        this.id = evaluacionId + "_" + estudianteId;
        this.evaluacionId = evaluacionId;
        this.estudianteId = estudianteId;
        this.nota = nota;
        this.registradoPor = registradoPor;
    }

    public String getId() { return id; }
    public String getEvaluacionId() { return evaluacionId; }
    public String getEstudianteId() { return estudianteId; }
    public Double getNota() { return nota; }
    public String getRegistradoPor() { return registradoPor; }
}
