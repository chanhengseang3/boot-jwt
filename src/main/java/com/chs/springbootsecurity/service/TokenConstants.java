package com.chs.springbootsecurity.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class TokenConstants {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationDateInMs:10000}")
    private int jwtExpirationInMs;

    @Value("${jwt.refreshExpirationDateInMs:500000}")
    private int refreshExpirationDateInMs;

}
