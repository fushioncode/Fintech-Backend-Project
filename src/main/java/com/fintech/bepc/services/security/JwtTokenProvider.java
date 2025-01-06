package com.fintech.bepc.services.security;

import com.fintech.bepc.model.dtos.AuthResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${spring.security.jwt.secret}")
    private String SECRET_KEY;

    private static final long EXPIRATION_TIME = 86400000L;

    @Value("${jwt-expiration-time-in-milli-seconds}")
    private Long jwTokenExpirationInMs;
    @Value("${jwt-refresh-token-expiration-in-milli-seconds}")
    private Long jwtRefreshTokenExpirationInMs;

    public AuthResponseDto generateToken(UserDetails userDetails){
        return createToken(userDetails);
    }

    @SuppressWarnings("deprecation")
    private AuthResponseDto createToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Date nowDate = Date.from(now);
        Date tokenExpirationDate = Date.from(now.plusMillis(jwTokenExpirationInMs));
        Date refreshTokenExpirationDate = Date.from(now.plusMillis(jwtRefreshTokenExpirationInMs));

        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", authorities)
//                .setIssuedAt(nowDate)
//                .setExpiration(tokenExpirationDate)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwTokenExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", authorities)
                .setIssuedAt(nowDate)
                .setExpiration(refreshTokenExpirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenExpiry(tokenExpirationDate)
                .refreshTokenExpiry(refreshTokenExpirationDate)
                .build();
    }


    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }



}
