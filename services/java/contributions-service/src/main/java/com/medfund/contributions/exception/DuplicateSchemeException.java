package com.medfund.contributions.exception;

public class DuplicateSchemeException extends RuntimeException {

    public DuplicateSchemeException(String name) {
        super("Scheme already exists: " + name);
    }
}
