//package com.fintech.bepc.config;
//
//import com.fintech.bepc.exceptions.APIException;
//import com.fintech.bepc.services.security.CustomUserDetailsService;
//import com.fintech.bepc.services.security.JwtTokenProvider;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.io.IOException;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//class JwtAuthenticationFilterTest {
//
//    @Mock
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Mock
//    private CustomUserDetailsService customUserDetailsService;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private HttpServletResponse response;
//
//    @Mock
//    private FilterChain filterChain;
//
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
//    }
//
//    @Test
//    void testDoFilterInternalValidToken() throws ServletException, IOException {
//        String token = "validToken";
//        String username = "testUser";
//
//        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(username);
//        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(mock(UserDetails.class));
//
//        ReflectionTestUtils.setField(jwtAuthenticationFilter, "secretKey", "secretKey");
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain).doFilter(request, response);
//        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//
//    @Test
//    void testDoFilterInternalInvalidToken() throws ServletException, IOException {
//        String token = "invalidToken";
//        String username = "testUser";
//
//        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(username);
//        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(mock(UserDetails.class));
//
//        ReflectionTestUtils.setField(jwtAuthenticationFilter, "secretKey", "secretKey");
//        assertThrows(APIException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
//    }
//
//    @Test
//    void testDoFilterInternalExpiredToken() throws ServletException, IOException {
//        String token = "expiredToken";
//        String username = "testUser";
//
//        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(username);
//        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(mock(UserDetails.class));
//
//        ReflectionTestUtils.setField(jwtAuthenticationFilter, "secretKey", "secretKey");
//        assertThrows(APIException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
//    }
//
//    @Test
//    void testDoFilterInternalNoAuthorizationHeader() throws ServletException, IOException {
//        when(request.getHeader("Authorization")).thenReturn(null);
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//        verify(filterChain).doFilter(request, response);
//    }
//}
