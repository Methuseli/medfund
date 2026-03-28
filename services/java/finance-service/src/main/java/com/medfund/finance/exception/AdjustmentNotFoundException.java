package com.medfund.finance.exception;

import java.util.UUID;

public class AdjustmentNotFoundException extends RuntimeException {

    public AdjustmentNotFoundException(UUID id) {
        super("Adjustment not found: " + id);
    }
}
