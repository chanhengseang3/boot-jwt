package com.chs.springbootsecurity.exception;

public class TokenEmptyException extends RuntimeException{
    public TokenEmptyException() {
        super("Access token is empty");
    }
}
