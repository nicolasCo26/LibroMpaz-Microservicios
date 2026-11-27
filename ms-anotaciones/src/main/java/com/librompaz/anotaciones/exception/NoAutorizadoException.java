package com.librompaz.anotaciones.exception;

/** Se traduce a 403 Forbidden: solo quien firmó una anotación puede corregirla o eliminarla. */
public class NoAutorizadoException extends RuntimeException {
    public NoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
