package com.medfund.user.exception;

public class DuplicateRoleException extends RuntimeException {
    public DuplicateRoleException(String name) {
        super("Role already exists: " + name);
    }
}
