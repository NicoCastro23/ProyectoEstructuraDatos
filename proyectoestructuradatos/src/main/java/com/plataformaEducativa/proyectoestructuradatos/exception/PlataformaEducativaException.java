package com.plataformaEducativa.proyectoestructuradatos.exception;

// Excepción base para todas las excepciones de negocio
public abstract class PlataformaEducativaException extends RuntimeException {

    public PlataformaEducativaException(String message) {
        super(message);
    }

    public PlataformaEducativaException(String message, Throwable cause) {
        super(message, cause);
    }
}