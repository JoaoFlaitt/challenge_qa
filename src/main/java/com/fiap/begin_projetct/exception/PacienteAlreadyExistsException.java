package com.fiap.begin_projetct.exception;

public class PacienteAlreadyExistsException extends RuntimeException {
    public PacienteAlreadyExistsException(String message) {
        super(message);
    }
}
