package com.chs.springbootsecurity.config;

import com.chs.springbootsecurity.exception.MyExceptionData;
import com.chs.springbootsecurity.service.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.time.LocalDateTime;

@Slf4j
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
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                var e = MyExceptionData.builder()
                        .message("Access token is empty")
                        .path(request.getRequestURI())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .time(LocalDateTime.now())
                        .build();
                response.getWriter().write(ApplicationConfiguration.OBJECT_MAPPER.writeValueAsString(e));
            } else if (jwtUtil.validateToken(jwtToken)) {
                var userDetails = new User(jwtUtil.getUsernameFromToken(jwtToken), "", jwtUtil.getRolesFromToken(jwtToken));
                var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                chain.doFilter(request, response);
            } else {
                // using refresh token
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                var e = MyExceptionData.builder()
                        .message("Violation use of refresh token")
                        .path(request.getRequestURI())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .time(LocalDateTime.now())
                        .build();
                response.getWriter().write(ApplicationConfiguration.OBJECT_MAPPER.writeValueAsString(e));
            }
        } catch (ExpiredJwtException | BadCredentialsException ex) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            var e = MyExceptionData.builder()
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .time(LocalDateTime.now())
                    .build();
            response.getWriter().write(ApplicationConfiguration.OBJECT_MAPPER.writeValueAsString(e));
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}