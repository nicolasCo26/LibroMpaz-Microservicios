package com.librompaz.calificaciones.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Cuerpo de PUT /api/evaluaciones/{id}/calificaciones: la planilla COMPLETA de la evaluación.
 * Las notas que no vienen en la lista se eliminan (el docente las dejó en blanco).
 */
public record GuardarNotasRequest(
        @NotBlank(message = "Falta quién registra las notas") String registradoPor,
        @NotNull(message = "La lista de notas es obligatoria") List<@Valid NotaRequest> notas
) {
}
