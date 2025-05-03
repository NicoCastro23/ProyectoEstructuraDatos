package com.plataformaEducativa.proyectoestructuradatos.exception;

public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException() {
        super("Operación no autorizada");
    }

    public UnauthorizedOperationException(String message) {
        super(message);
    }

    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedOperationException(Throwable cause) {
        super(cause);
    }
}

