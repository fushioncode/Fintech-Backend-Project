package com.fintech.bepc.services.security;

import com.fintech.bepc.model.dtos.AuthResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetails mockUserDetails;

    private static final String SECRET_KEY = "7f3b9f4ad9cfb8ea4270d1be3786a6c89edc3fe56ad9c7b5ea918fe3f9a29bc1c4d7e60bb6a7f3b0dc8b24e62a12e6f1";
    private static final long TOKEN_EXPIRATION_TIME = 86400000L; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 172800000L; // 48 hours

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        jwtTokenProvider = new JwtTokenProvider();

        // Inject private fields using reflection
        setPrivateField(jwtTokenProvider, "SECRET_KEY", SECRET_KEY);
        setPrivateField(jwtTokenProvider, "jwTokenExpirationInMs", TOKEN_EXPIRATION_TIME);
        setPrivateField(jwtTokenProvider, "jwtRefreshTokenExpirationInMs", REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void generateToken_ShouldReturnAuthResponseWithCorrectValues() {
        when(mockUserDetails.getUsername()).thenReturn("testuser@example.com");
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());

        AuthResponseDto response = jwtTokenProvider.generateToken(mockUserDetails);
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isNotEmpty();
        assertThat(response.getTokenExpiry()).isAfter(Date.from(Instant.now()));
        assertThat(response.getRefreshTokenExpiry()).isAfter(response.getTokenExpiry());
    }

//    @Test
//    void generateToken_ShouldIncludeRolesInTokenClaims() {
//        GrantedAuthority authority = mock(GrantedAuthority.class);
//        when(authority.getAuthority()).thenReturn("ROLE_ADMIN");
//
//        when(mockUserDetails.getUsername()).thenReturn("admin@example.com");
//        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);
//        when(mockUserDetails.getAuthorities()).thenReturn(authorities);
//        AuthResponseDto response = jwtTokenProvider.generateToken(mockUserDetails);
//
//        assertThat(response.getToken()).isNotEmpty();
//        Claims claims = Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .parseClaimsJws(response.getToken())
//                .getBody();
//        assertThat(claims.get("roles")).isEqualTo("ROLE_ADMIN");
//    }



    @Test
    void getEmailFromToken_ShouldReturnCorrectEmail() {
        String testEmail = "testuser@example.com";
        String token = Jwts.builder()
                .setSubject(testEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        String email = jwtTokenProvider.getEmailFromToken(token);
        assertThat(email).isEqualTo(testEmail);
    }

    @Test
    void getEmailFromToken_ShouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(Exception.class, () -> jwtTokenProvider.getEmailFromToken(invalidToken));
    }

    @Test
    void generateToken_ShouldThrowExceptionForNullUserDetails() {
        UserDetails nullUserDetails = null;
        assertThrows(NullPointerException.class, () -> jwtTokenProvider.generateToken(nullUserDetails));
    }

    @Test
    void createToken_ShouldRespectExpirationSettings() {
        when(mockUserDetails.getUsername()).thenReturn("testuser@example.com");
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());

        AuthResponseDto response = jwtTokenProvider.generateToken(mockUserDetails);
        Date now = new Date();
        assertThat(response.getTokenExpiry().getTime())
                .isGreaterThanOrEqualTo(now.getTime() + TOKEN_EXPIRATION_TIME - 1000);
        assertThat(response.getRefreshTokenExpiry().getTime())
                .isGreaterThanOrEqualTo(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME - 1000);
    }
}
