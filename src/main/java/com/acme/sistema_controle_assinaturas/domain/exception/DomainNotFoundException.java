package com.acme.sistema_controle_assinaturas.domain.exception;

public class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException(String message) {
        super(message);
    }
}
