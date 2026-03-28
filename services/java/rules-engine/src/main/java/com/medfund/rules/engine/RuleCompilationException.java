package com.medfund.rules.engine;

/**
 * Thrown when DRL compilation fails due to errors in the generated rule definitions.
 */
public class RuleCompilationException extends RuntimeException {

    public RuleCompilationException(String message) {
        super(message);
    }

    public RuleCompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}
