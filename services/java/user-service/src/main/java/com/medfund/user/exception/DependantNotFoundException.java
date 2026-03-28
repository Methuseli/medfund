package com.medfund.user.exception;

import java.util.UUID;

public class DependantNotFoundException extends RuntimeException {
    public DependantNotFoundException(UUID id) {
        super("Dependant not found: " + id);
    }
}
