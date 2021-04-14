package com.chs.springbootsecurity.config;

import com.chs.springbootsecurity.data.AuthenticationExceptionData;
import com.chs.springbootsecurity.exception.TokenEmptyException;
import com.chs.springbootsecurity.exception.TokenViolationException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.chs.springbootsecurity.config.ApplicationConfiguration.OBJECT_MAPPER;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Exception exception = (Exception) request.getAttribute("exception");

        if (exception instanceof ExpiredJwtException
                || exception instanceof BadCredentialsException
                || exception instanceof TokenEmptyException) {
            var e = AuthenticationExceptionData.builder()
                    .message(exception.getMessage())
                    .path(request.getRequestURI())
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .time(LocalDateTime.now())
                    .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(e));
        } else if (exception instanceof TokenViolationException) {
            var e = AuthenticationExceptionData.builder()
                    .message(exception.getMessage())
                    .path(request.getRequestURI())
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .time(LocalDateTime.now())
                    .build();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(e));
        } else {
            String message;
            if (authException.getCause() != null) {
                message = authException.getCause().toString() + " " + authException.getMessage();
            } else {
                message = authException.getMessage();
            }
            var e = AuthenticationExceptionData.builder()
                    .message(message)
                    .path(request.getRequestURI())
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .time(LocalDateTime.now())
                    .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(e));
        }
    }
}
