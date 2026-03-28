package com.medfund.contributions.exception;

import java.util.UUID;

public class SchemeNotFoundException extends RuntimeException {

    public SchemeNotFoundException(UUID id) {
        super("Scheme not found: " + id);
    }
}
