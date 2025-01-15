package com.fintech.bepc.config;

import com.fintech.bepc.exceptions.APIException;
import com.fintech.bepc.services.security.CustomUserDetailsService;
import com.fintech.bepc.services.security.JwtTokenProvider;
import com.fintech.bepc.services.serviceImpl.IUserService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(IUserService.class);

    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    private JwtTokenProvider jwtTokenProvider;
    private CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider provider, CustomUserDetailsService userDetailsService){
        this.jwtTokenProvider=provider;
        this.customUserDetailsService=userDetailsService;
    }

    private static final String EXCEPTION = "exception";
    final Map<String, Object> errorMap = Map.of("success", false, "message",
            "Could not validate your credentials");


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

        String username = null;
        String token = null;
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = getTokenFromRequest(request);

            try {
                username = jwtTokenProvider.getEmailFromToken(token);
            } catch (ExpiredJwtException e) {
                log.error("JWT token expired: {}", e.getMessage());
                throw new APIException("JWT token has expired", HttpServletResponse.SC_UNAUTHORIZED);
            } catch (JwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                throw new APIException("Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED);
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
                throw new APIException("JWT token is not valid", HttpServletResponse.SC_UNAUTHORIZED);
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

}

