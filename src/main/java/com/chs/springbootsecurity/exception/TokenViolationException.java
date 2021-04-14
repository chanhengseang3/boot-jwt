package com.chs.springbootsecurity.exception;

public class TokenViolationException extends RuntimeException {
    public TokenViolationException() {
        super("Violation use of refresh token");
    }
}
