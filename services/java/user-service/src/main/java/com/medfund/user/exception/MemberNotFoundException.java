package com.medfund.user.exception;

import java.util.UUID;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(UUID id) {
        super("Member not found: " + id);
    }
    public MemberNotFoundException(String memberNumber) {
        super("Member not found: " + memberNumber);
    }
}
