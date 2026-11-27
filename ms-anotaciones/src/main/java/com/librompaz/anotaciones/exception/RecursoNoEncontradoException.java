package com.librompaz.anotaciones.exception;

/** Se traduce a 404 Not Found en ManejadorErrores. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
