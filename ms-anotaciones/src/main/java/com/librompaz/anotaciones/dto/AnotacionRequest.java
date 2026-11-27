package com.librompaz.anotaciones.dto;

import com.librompaz.anotaciones.model.TipoAnotacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de POST y PUT /api/anotaciones. El id es opcional (si no viene, lo genera el servidor).
 * En el PUT, firmadoPor debe ser quien registró la anotación.
 */
public record AnotacionRequest(
        String id,
        @NotBlank(message = "El estudiante es obligatorio") String estudianteId,
        @NotNull(message = "El tipo es obligatorio") TipoAnotacion tipo,
        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 300, message = "La descripción debe tener entre 10 y 300 caracteres")
        String descripcion,
        @NotBlank(message = "Falta quién firma la anotación") String firmadoPor
) {
}
