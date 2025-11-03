package com.grup14.luterano.auth.infrastructure;

public class AuthenticateException extends RuntimeException {
    public String mensajeNuevo;

    public AuthenticateException(String mensaje) {
        mensajeNuevo = mensaje;
    }

    @Override
    public String getMessage() {
        return mensajeNuevo;
    }

}
