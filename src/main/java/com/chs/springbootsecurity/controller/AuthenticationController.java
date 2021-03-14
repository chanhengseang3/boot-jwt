package com.chs.springbootsecurity.controller;

import com.chs.springbootsecurity.data.AuthenticationData;
import com.chs.springbootsecurity.data.AuthenticationRequest;
import com.chs.springbootsecurity.service.CustomUserDetailsService;
import com.chs.springbootsecurity.service.JwtUtil;
import com.chs.springbootsecurity.service.TokenConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Slf4j
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final TokenConfig tokenConfig;
    private final JwtUtil jwtUtil;

    @PostMapping("token")
    public AuthenticationData createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authenticationToken);
        UserDetails userdetails = userDetailsService.loadUserByUsername(request.getUsername());
        return jwtUtil.generateToken(userdetails);
    }

    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String token) {

        // get claims
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(tokenConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is expired");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid refresh token");
        }

        // validate token type
        if (!claims.containsKey("refreshToken")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided token is not refresh token");
        }

        var expectedMap = getMapFromIoJsonWebTokenClaims(claims);
        var subject = expectedMap.get("sub").toString();

        // generate refresh token
        var refreshToken = jwtUtil.generateRefreshToken(expectedMap, subject);

        // generate token
        expectedMap.remove("refreshToken");
        var authToken = jwtUtil.generateToken(expectedMap, subject);

        return ResponseEntity.ok(new AuthenticationData(authToken, refreshToken));
    }

    private Map<String, Object> getMapFromIoJsonWebTokenClaims(Claims claims) {
        Map<String, Object> expectedMap = new HashMap<>();
        for (Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

}
