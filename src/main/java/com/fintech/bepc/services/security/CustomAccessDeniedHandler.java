package com.fintech.bepc.services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.bepc.model.dtos.APIError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.info(
                "ACCESS DENIED ERROR: {}",accessDeniedException.getMessage()
        );
        APIError apiError = APIError.builder()
                .message(accessDeniedException.getMessage())
                .error(accessDeniedException.getMessage())
                .statusCode(HttpServletResponse.SC_FORBIDDEN)
                .path(request.getRequestURI())
                .build();

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        PrintWriter writer = response.getWriter();
        writer.write(mapper.writeValueAsString(apiError));
        writer.flush();
        writer.close();
    }
}