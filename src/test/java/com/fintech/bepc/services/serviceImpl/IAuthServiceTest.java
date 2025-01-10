package com.fintech.bepc.services.serviceImpl;

import com.fintech.bepc.model.dtos.AuthRequestDto;
import com.fintech.bepc.model.dtos.AuthResponseDto;
import com.fintech.bepc.model.dtos.UserRequestDto;
import com.fintech.bepc.model.entities.User;
import com.fintech.bepc.repositories.UserRepository;
import com.fintech.bepc.services.security.CustomUserDetailsService;
import com.fintech.bepc.services.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IAuthServiceTest {

    @InjectMocks
    private IAuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponseDto() {
        // Arrange
        AuthRequestDto authRequestDto = new AuthRequestDto("test@example.com", "password123");
        var userDetails = mock(org.springframework.security.core.userdetails.User.class);
        when(customUserDetailsService.loadUserByUsername(authRequestDto.getEmail())).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("$2a$10$validHashedPassword");
        when(passwordEncoder.matches(authRequestDto.getPassword(), userDetails.getPassword())).thenReturn(true);
        AuthResponseDto expectedResponse = new AuthResponseDto("jwtToken", null, null, "refreshToken");
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn(expectedResponse);

        // Act
        AuthResponseDto actualResponse = authService.login(authRequestDto);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(customUserDetailsService).loadUserByUsername(authRequestDto.getEmail());
        verify(jwtTokenProvider).generateToken(userDetails);
    }

    @Test
    void login_InvalidPassword_ThrowsIllegalArgumentException() {
        // Arrange
        AuthRequestDto authRequestDto = new AuthRequestDto("test@example.com", "wrongPassword");
        var userDetails = mock(org.springframework.security.core.userdetails.User.class);
        when(customUserDetailsService.loadUserByUsername(authRequestDto.getEmail())).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("$2a$10$validHashedPassword");
        when(passwordEncoder.matches(authRequestDto.getPassword(), userDetails.getPassword())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.login(authRequestDto));
        assertEquals("Invalid Username and Password", exception.getMessage());
    }

    @Test
    void registerUser_ValidDetails_SavesUser() {
        // Arrange
        UserRequestDto userRequestDto = new UserRequestDto("user@example.com", "password123", "John Doe", "1234567890");
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("$2a$10$hashedPassword");

        // Act
        authService.registerUser(userRequestDto);

        // Assert
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(userRequestDto.getEmail()) &&
                        user.getFullName().equals(userRequestDto.getFullName()) &&
                        user.getPassword().equals("$2a$10$hashedPassword") &&
                        user.getRole() == User.Role.USER
        ));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsIllegalArgumentException() {
        // Arrange
        UserRequestDto userRequestDto = new UserRequestDto("user@example.com", "password123", "John Doe", "1234567890");
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.registerUser(userRequestDto));
        assertEquals("Email is already registered", exception.getMessage());
    }

    @Test
    void registerAdmin_ValidDetails_SavesAdmin() {
        // Arrange
        UserRequestDto userRequestDto = new UserRequestDto("admin@example.com", "password123", "Jane Doe", "9876543210");
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("$2a$10$hashedPassword");

        // Act
        authService.registerAdmin(userRequestDto);

        // Assert
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(userRequestDto.getEmail()) &&
                        user.getFullName().equals(userRequestDto.getFullName()) &&
                        user.getPassword().equals("$2a$10$hashedPassword") &&
                        user.getRole() == User.Role.ADMIN
        ));
    }

    @Test
    void registerAdmin_EmailAlreadyExists_ThrowsIllegalArgumentException() {
        // Arrange
        UserRequestDto userRequestDto = new UserRequestDto("admin@example.com", "password123", "Jane Doe", "9876543210");
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.registerAdmin(userRequestDto));
        assertEquals("Email is already registered", exception.getMessage());
    }
}
