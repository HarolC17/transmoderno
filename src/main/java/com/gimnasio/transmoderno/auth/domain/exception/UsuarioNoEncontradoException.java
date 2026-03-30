package com.gimnasio.transmoderno.auth.domain.exception;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String correo) {
        super("Usuario no encontrado: " + correo);
    }
}