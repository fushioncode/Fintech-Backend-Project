package com.fintech.bepc.services.serviceImpl;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.UserRequestDto;
import com.fintech.bepc.model.dtos.UserResponseDto;
import com.fintech.bepc.model.entities.User;
import com.fintech.bepc.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private IUserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUser_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPhoneNumber("+234123456789");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ApiResponse<UserResponseDto> response = userService.getUser(userId);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(user.getEmail(), response.getData().getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUser_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.getUser(userId));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUser_Success() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setPhoneNumber("+234123456789");

        UserRequestDto updateRequest = new UserRequestDto(
                "new@example.com",
                "newpassword",
                "New User",
                "+234987654321"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ApiResponse<UserResponseDto> response = userService.updateUser(userId, updateRequest);

        assertTrue(response.isSuccess());
        assertEquals("new@example.com", response.getData().getEmail());
        assertEquals("+234987654321", response.getData().getPhoneNumber());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        Long userId = 1L;
        UserRequestDto updateRequest = new UserRequestDto(
                "new@example.com",
                "newpassword",
                "New User",
                "+234987654321"
        );
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, updateRequest));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));
        assertEquals("User does not exist", exception.getMessage());
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void testGetAllUsers_Success() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setFullName("User One");
        user1.setPhoneNumber("+234123456789");
        user1.setPassword("password1");
        user1.setActive(true);
        user1.setRole(User.Role.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setFullName("User Two");
        user2.setPhoneNumber("+234987654321");
        user2.setPassword("password2");
        user2.setActive(true);
        user2.setRole(User.Role.ADMIN);

        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        ApiResponse<List<UserResponseDto>> response = userService.getAllUsers();

        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().size());
        assertEquals("user1@example.com", response.getData().get(0).getEmail());
        assertEquals("user2@example.com", response.getData().get(1).getEmail());
        verify(userRepository, times(1)).findAll();
    }

}
