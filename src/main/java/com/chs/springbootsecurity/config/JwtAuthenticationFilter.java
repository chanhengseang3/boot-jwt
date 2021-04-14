package com.chs.springbootsecurity.config;

import com.chs.springbootsecurity.exception.TokenEmptyException;
import com.chs.springbootsecurity.exception.TokenViolationException;
import com.chs.springbootsecurity.service.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String jwtToken = extractJwtFromRequest(request);
            if (!StringUtils.hasText(jwtToken)) {
                // token is empty
                request.setAttribute("exception", new TokenEmptyException());
            } else if (jwtUtil.validateToken(jwtToken)) {
                var userDetails = new User(jwtUtil.getUsernameFromToken(jwtToken), "", jwtUtil.getRolesFromToken(jwtToken));
                var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                // using refresh token
                request.setAttribute("exception", new TokenViolationException());
            }
        } catch (ExpiredJwtException | BadCredentialsException ex) {
            request.setAttribute("exception", ex);
        }
        chain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}