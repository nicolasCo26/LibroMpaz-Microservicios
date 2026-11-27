package com.librompaz.calificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Cuerpo de POST y PUT /api/evaluaciones. El id es opcional: si no viene, lo genera el servidor. */
public record EvaluacionRequest(
        String id,
        @NotBlank(message = "El curso es obligatorio") String cursoId,
        @NotBlank(message = "La asignatura es obligatoria") String asignaturaId,
        @NotBlank(message = "El nombre del instrumento es obligatorio")
        @Size(min = 3, max = 60, message = "El nombre debe tener entre 3 y 60 caracteres")
        String nombreInstrumento,
        @NotBlank(message = "La fecha es obligatoria")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "La fecha debe tener el formato AAAA-MM-DD")
        String fecha
) {
}
