package com.librompaz.calificaciones.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Una nota dentro de PUT /api/evaluaciones/{id}/calificaciones. */
public record NotaRequest(
        @NotBlank(message = "El estudiante es obligatorio") String estudianteId,
        @NotNull(message = "La nota es obligatoria")
        @DecimalMin(value = "1.0", message = "La nota mínima es 1,0")
        @DecimalMax(value = "7.0", message = "La nota máxima es 7,0")
        Double nota
) {
}
