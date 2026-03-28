package com.medfund.claims.exception;

import java.util.UUID;

public class TariffNotFoundException extends RuntimeException {

    public TariffNotFoundException(UUID id) {
        super("Tariff not found: " + id);
    }

    public TariffNotFoundException(String code) {
        super("Tariff code not found: " + code);
    }
}
