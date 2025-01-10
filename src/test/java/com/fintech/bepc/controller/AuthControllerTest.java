package com.fintech.bepc.controller;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.AuthRequestDto;
import com.fintech.bepc.model.dtos.AuthResponseDto;
import com.fintech.bepc.model.dtos.UserRequestDto;
import com.fintech.bepc.services.AuthService;
import com.fintech.bepc.services.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        AuthRequestDto authRequestDto = new AuthRequestDto("test@example.com", "password");
        AuthResponseDto mockResponse = AuthResponseDto.builder()
                .token("testToken")
                .tokenExpiry(null)
                .refreshToken("refreshToken")
                .refreshTokenExpiry(null)
                .build();

        when(authService.login(authRequestDto)).thenReturn(mockResponse);

        ResponseEntity<AuthResponseDto> response = authController.login(authRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(authService, times(1)).login(authRequestDto);
    }

    @Test
    void testRegisterUser_Success() {
        UserRequestDto userRequestDto = new UserRequestDto("test@example.com", "password", "Test", "User");

        doNothing().when(authService).registerUser(userRequestDto);

        ResponseEntity<?> response = authController.registerUser(userRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
        assertEquals("Registration successful", ((ApiResponse) response.getBody()).getData());
        verify(authService, times(1)).registerUser(userRequestDto);
    }

    @Test
    void testRegisterAdmin_Success() {
        UserRequestDto adminRequestDto = new UserRequestDto("admin@example.com", "password", "Admin", "User");

        doNothing().when(authService).registerAdmin(adminRequestDto);

        ResponseEntity<?> response = authController.registerAdmin(adminRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
        assertEquals("Registration successful", ((ApiResponse) response.getBody()).getData());
        verify(authService, times(1)).registerAdmin(adminRequestDto);
    }

//    @Test
//    void testLogin_InvalidInput() {
//        AuthRequestDto authRequestDto = new AuthRequestDto("invalid-email", "");
//
//        MethodArgumentNotValidException exception = assertThrows(MethodArgumentNotValidException.class, () -> {
//            authController.login(authRequestDto);
//        });
//
//        assertNotNull(exception);
//    }
//
//    @Test
//    void testRegisterUser_InvalidInput() {
//        UserRequestDto invalidRequestDto = new UserRequestDto("", "", "", "");
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            authController.registerUser(invalidRequestDto);
//        });
//
//        assertNotNull(exception);
//    }
//
//    @Test
//    void testRegisterAdmin_InvalidInput() {
//        UserRequestDto invalidRequestDto = new UserRequestDto(null, "", "", "");
//
//        MethodArgumentNotValidException exception = assertThrows(MethodArgumentNotValidException.class, () -> {
//            authController.registerAdmin(invalidRequestDto);
//        });
//
//        assertNotNull(exception);
//    }

    @Test
    void testLogin_ServiceException() {
        AuthRequestDto authRequestDto = new AuthRequestDto("test@example.com", "password");

        when(authService.login(authRequestDto)).thenThrow(new RuntimeException("Service error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.login(authRequestDto);
        });

        assertEquals("Service error", exception.getMessage());
        verify(authService, times(1)).login(authRequestDto);
    }

    @Test
    void testRegisterUser_ServiceException() {
        UserRequestDto userRequestDto = new UserRequestDto("test@example.com", "password", "Test", "User");

        doThrow(new RuntimeException("Service error")).when(authService).registerUser(userRequestDto);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.registerUser(userRequestDto);
        });

        assertEquals("Service error", exception.getMessage());
        verify(authService, times(1)).registerUser(userRequestDto);
    }

    @Test
    void testRegisterAdmin_ServiceException() {
        UserRequestDto adminRequestDto = new UserRequestDto("admin@example.com", "password", "Admin", "User");

        doThrow(new RuntimeException("Service error")).when(authService).registerAdmin(adminRequestDto);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.registerAdmin(adminRequestDto);
        });

        assertEquals("Service error", exception.getMessage());
        verify(authService, times(1)).registerAdmin(adminRequestDto);
    }
}
