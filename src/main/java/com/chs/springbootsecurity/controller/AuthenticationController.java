package com.chs.springbootsecurity.controller;

import com.chs.springbootsecurity.data.AuthenticationRequest;
import com.chs.springbootsecurity.data.AuthenticationResponse;
import com.chs.springbootsecurity.data.UserData;
import com.chs.springbootsecurity.service.CustomUserDetailsService;
import com.chs.springbootsecurity.service.JwtUtil;
import com.chs.springbootsecurity.service.TokenConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final TokenConstants tokenConstants;
    private final JwtUtil jwtUtil;

    @PostMapping("authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        authenticationManager.authenticate(authenticationToken);
        UserDetails userdetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = jwtUtil.generateToken(userdetails);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @PostMapping("register")
    public ResponseEntity<?> saveUser(@RequestBody UserData user) {
        return ResponseEntity.ok(userDetailsService.save(user));
    }

    @PostMapping("refreshtoken")
    public ResponseEntity<?> refreshToken(@RequestParam String token) {

        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(tokenConstants.getSecret())
                    .parseClaimsJwt(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }

        Map<String, Object> expectedMap = getMapFromIoJsonWebTokenClaims(claims);

        String authToken = jwtUtil.generateRefreshToken(expectedMap, expectedMap.get("sub").toString());
        return ResponseEntity.ok(new AuthenticationResponse(authToken));
    }

    private Map<String, Object> getMapFromIoJsonWebTokenClaims(Claims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

}
