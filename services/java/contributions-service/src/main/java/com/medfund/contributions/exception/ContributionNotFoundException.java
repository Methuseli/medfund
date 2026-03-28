package com.medfund.contributions.exception;

import java.util.UUID;

public class ContributionNotFoundException extends RuntimeException {

    public ContributionNotFoundException(UUID id) {
        super("Contribution not found: " + id);
    }
}
