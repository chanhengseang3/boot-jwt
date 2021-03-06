package com.chs.springbootsecurity.service;

import com.chs.springbootsecurity.data.AuthenticationData;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JwtUtil {

    @Autowired
    private TokenConfig tokenConfig;

    public AuthenticationData generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        var roles = userDetails.getAuthorities();

        if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            claims.put("isAdmin", true);
        }
        if (roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            claims.put("isUser", true);
        }
        var token = generateToken(claims, userDetails.getUsername());

        // add flag
        claims.put("refreshToken", true);
        var refreshToken = generateRefreshToken(claims, userDetails.getUsername());
        return new AuthenticationData(token, refreshToken);
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenConfig.getJwtExpirationInMs()))
                .signWith(SignatureAlgorithm.HS512, tokenConfig.getSecret()).compact();
    }

    public String generateRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenConfig.getRefreshExpirationDateInMs()))
                .signWith(SignatureAlgorithm.HS512, tokenConfig.getSecret()).compact();
    }

    public boolean validateToken(String authToken) {
        try {
            var claims = Jwts.parser()
                    .setSigningKey(tokenConfig.getSecret())
                    .parseClaimsJws(authToken)
                    .getBody();
            return !claims.containsKey("refreshToken");
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(tokenConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();

        Boolean isAdmin = claims.get("isAdmin", Boolean.class);
        Boolean isUser = claims.get("isUser", Boolean.class);

        if (isAdmin != null && isAdmin) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if (isUser != null && isUser) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of();
    }
}
