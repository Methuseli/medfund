package com.medfund.user.exception;

public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException(String field, String value) {
        super("Member already exists with " + field + ": " + value);
    }
}
