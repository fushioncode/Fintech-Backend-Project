package com.fintech.bepc.services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.bepc.model.dtos.APIError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;


@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Authentication error: {}", authException.getCause()); // Log the error message

        APIError apiError;

        // Check if the cause of the authentication exception is a JWT-related exception
        Throwable cause = authException.getCause();
        if (cause instanceof ExpiredJwtException) {
            apiError = APIError.builder()
                    .message("JWT token has expired")
                    .error("JWT token expired")
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED) // 401
                    .path(request.getRequestURI())
                    .build();
        } else if (cause instanceof JwtException) {
            apiError = APIError.builder()
                    .message("Invalid JWT token")
                    .error("JWT token invalid")
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED) // 401
                    .path(request.getRequestURI())
                    .build();
        }else if (authException instanceof InsufficientAuthenticationException) {
            apiError = APIError.builder()
                    .message("Session expired please login again")
                    .error(authException.getLocalizedMessage())
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED) // 401
                    .path(request.getRequestURI())
                    .build();
        } else {
            // For other authentication-related issues
            apiError = APIError.builder()
                    .message(authException.getMessage())
                    .error(authException.getCause() != null ? authException.getCause().getMessage() : "Authentication failure")
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED) // 401
                    .path(request.getRequestURI())
                    .build();
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Ensure it's 401 for authentication issues

        PrintWriter writer = response.getWriter();
        writer.write(mapper.writeValueAsString(apiError));
        writer.flush();
        writer.close();
    }
}