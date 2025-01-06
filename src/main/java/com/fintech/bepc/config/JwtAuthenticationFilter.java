package com.fintech.bepc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.bepc.exception.APIException;
import com.fintech.bepc.services.security.CustomUserDetailsService;
import com.fintech.bepc.services.security.JwtTokenProvider;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;

//@WebFilter
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private  final ObjectMapper objectMapper;

    private static final String EXCEPTION = "exception";
    final Map<String, Object> errorMap = Map.of("success", false, "message",
            "Could not validate your credentials");

//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            @NotNull HttpServletResponse response,
//            @NotNull FilterChain filterChain) throws IOException {
//
//        // Handle pre-flight OPTIONS requests
//        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            return;
//        }
//
//        final String AUTH_HEADER_KEY = "Authorization";
//        final String authorizationHeader = request.getHeader(AUTH_HEADER_KEY);
//
//        String username = null;
//        String jwtToken = "";
//
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            jwtToken = authorizationHeader.substring(7);
//            try {
//                username = jwtTokenProvider.getEmailFromToken(jwtToken);
//            } catch (IllegalArgumentException | BadCredentialsException e) {
//                logger.error("::::: IllegalArgumentException or BadCredentialsException ==>" + e.getLocalizedMessage());
//                request.setAttribute(EXCEPTION, "Bad credentials - Could not process token");
//            } catch (ExpiredJwtException e) {
//                doProcessIfRequestIsForRefreshToken(request, e);
//            } catch (Exception e) {
//                logger.error("::::: Unable to get JWT Token ==>" + e.getMessage());
//                request.setAttribute(EXCEPTION, "Could not process token " + e.getMessage());
//            }
//
//        } else {
//            logger.warn("JWT Token does not begin with Bearer String");
////            request.setAttribute(EXCEPTION, "Bearer Authorization header is required");
//            throw new IllegalArgumentException("Bearer Authorization header is required");
//        }
//
//        doCredentialValidation(request, username, jwtToken);
//        defineWhetherToContinueBasedOnTokenValidation(request, response, filterChain);
//    }
//
//    private void doCredentialValidation(HttpServletRequest request, String username, String jwtToken) {
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this
//                    .customUserDetailsService.loadUserByUsername(username);
//
//            Boolean isValid = validateToken(jwtToken);
//            if (Boolean.TRUE.equals(isValid)) {
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails, null, userDetails.getAuthorities());
//                usernamePasswordAuthenticationToken.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request)
//                );
//                SecurityContextHolder.getContext()
//                        .setAuthentication(usernamePasswordAuthenticationToken);
//            }
//        }
//    }
//
//    private void defineWhetherToContinueBasedOnTokenValidation(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws IOException {
//        try {
//            filterChain.doFilter(request, response);
//        } catch (final AccessDeniedException | ServletException e) {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.getWriter()
//                    .write(new ObjectMapper().writeValueAsString(errorMap));
//        }
//    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Handle pre-flight OPTIONS requests
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String requestURI = request.getRequestURI();
        log.info("CURRENT PATH: {}", requestURI);

//        boolean isWhitelisted = Arrays.stream(whitelistedPaths)
//                .anyMatch(requestURI::startsWith);
//
//        // If it's a whitelisted path, just proceed
//        if (isWhitelisted) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String username = null;
        String token = null;
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = getTokenFromRequest(request);

            try {
                username = jwtTokenProvider.getEmailFromToken(token); // Parse the username from the token
            } catch (ExpiredJwtException e) {
                log.error("JWT token expired: {}", e.getMessage());
                throw new APIException("JWT token has expired", HttpServletResponse.SC_UNAUTHORIZED); // Throwing custom exception
            } catch (JwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                throw new APIException("Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED); // Throwing custom exception
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Validate token before proceeding
            if (validateToken(token)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw new APIException("JWT token is not valid", HttpServletResponse.SC_UNAUTHORIZED); // Throwing custom exception
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    private void doProcessIfRequestIsForRefreshToken(HttpServletRequest request, ExpiredJwtException e) {
        logger.warn("::::: JWT Token has expired ==>" + e.getMessage());
        String isRefreshToken = request.getHeader("isRefreshToken");
        String requestURL = request.getRequestURL().toString();
        if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refresh/token")) {
            allowForRefreshToken(e, request);
        } else {
            logger.error("::::: Access Token has expired ==>" + e.getMessage());
            request.setAttribute(EXCEPTION, "Expired Token - Access Token has expired");
        }
    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        request.setAttribute("claims", ex.getClaims());
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
}

