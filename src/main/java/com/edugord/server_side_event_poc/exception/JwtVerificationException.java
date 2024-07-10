package com.edugord.server_side_event_poc.exception;

public class JwtVerificationException extends RuntimeException {
    public JwtVerificationException(String message) {
        super(message);
    }
}
