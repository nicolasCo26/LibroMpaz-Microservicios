package com.librompaz.anotaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Anotación de la hoja de vida de un estudiante. Tabla "anotaciones".
 * La "firma" es el docente que la registró (firmadoPor) + la fecha y hora que pone el servidor.
 */
@Entity
@Table(name = "anotaciones", indexes = @Index(columnList = "estudianteId"))
public class Anotacion {

    @Id
    private String id;

    @Column(nullable = false)
    private String estudianteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAnotacion tipo;

    @Column(nullable = false, length = 300)
    private String descripcion;

    /** AAAA-MM-DD HH:mm:ss, hora de Chile. */
    @Column(nullable = false, length = 19)
    private String fechaHora;

    @Column(nullable = false)
    private String firmadoPor;

    protected Anotacion() {
        // Requerido por JPA
    }

    public Anotacion(String id, String estudianteId, TipoAnotacion tipo, String descripcion,
                     String fechaHora, String firmadoPor) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.firmadoPor = firmadoPor;
    }

    public String getId() { return id; }
    public String getEstudianteId() { return estudianteId; }
    public TipoAnotacion getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public String getFechaHora() { return fechaHora; }
    public String getFirmadoPor() { return firmadoPor; }

    public void setTipo(TipoAnotacion tipo) { this.tipo = tipo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
