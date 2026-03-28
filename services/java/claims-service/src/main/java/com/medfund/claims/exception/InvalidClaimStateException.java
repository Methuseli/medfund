package com.medfund.claims.exception;

public class InvalidClaimStateException extends RuntimeException {

    public InvalidClaimStateException(String currentState, String targetState) {
        super("Cannot transition claim from " + currentState + " to " + targetState);
    }
}
