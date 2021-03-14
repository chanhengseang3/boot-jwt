package com.chs.springbootsecurity.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationData {
    private String token;
    private String refreshToken;
}
