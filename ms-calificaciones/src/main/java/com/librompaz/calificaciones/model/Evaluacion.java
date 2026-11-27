package com.librompaz.calificaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Instrumento de evaluación de un curso y una asignatura. Tabla "evaluaciones". */
@Entity
@Table(name = "evaluaciones")
public class Evaluacion {

    /** UUID que genera la app (o el servidor si no viene). */
    @Id
    private String id;

    @Column(nullable = false)
    private String cursoId;

    @Column(nullable = false)
    private String asignaturaId;

    @Column(nullable = false, length = 60)
    private String nombreInstrumento;

    /** Fecha en formato AAAA-MM-DD. */
    @Column(nullable = false, length = 10)
    private String fecha;

    protected Evaluacion() {
        // Requerido por JPA
    }

    public Evaluacion(String id, String cursoId, String asignaturaId, String nombreInstrumento, String fecha) {
        this.id = id;
        this.cursoId = cursoId;
        this.asignaturaId = asignaturaId;
        this.nombreInstrumento = nombreInstrumento;
        this.fecha = fecha;
    }

    public String getId() { return id; }
    public String getCursoId() { return cursoId; }
    public String getAsignaturaId() { return asignaturaId; }
    public String getNombreInstrumento() { return nombreInstrumento; }
    public String getFecha() { return fecha; }

    public void setCursoId(String cursoId) { this.cursoId = cursoId; }
    public void setAsignaturaId(String asignaturaId) { this.asignaturaId = asignaturaId; }
    public void setNombreInstrumento(String nombreInstrumento) { this.nombreInstrumento = nombreInstrumento; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
