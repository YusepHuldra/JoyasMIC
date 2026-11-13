package com.joyasmic.negocio;

/** Error inesperado (BD caída, disco, etc.). No se muestra el detalle al usuario. */
public class ErrorInternoException extends RuntimeException {
    public ErrorInternoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
