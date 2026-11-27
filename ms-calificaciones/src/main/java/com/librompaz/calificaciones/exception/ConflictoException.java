package com.librompaz.calificaciones.exception;

/** Se traduce a 409 Conflict en ManejadorErrores. */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
