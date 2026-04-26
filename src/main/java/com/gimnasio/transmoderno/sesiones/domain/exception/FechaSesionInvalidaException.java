package com.gimnasio.transmoderno.sesiones.domain.exception;

public class FechaSesionInvalidaException extends RuntimeException {
    public FechaSesionInvalidaException(String mensaje) {
        super(mensaje);
    }
}