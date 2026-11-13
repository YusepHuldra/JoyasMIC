package com.joyasmic.negocio;

/** Error "esperado" por datos inválidos del usuario; el mensaje se muestra tal cual en pantalla. */
public class ValidacionException extends Exception {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
