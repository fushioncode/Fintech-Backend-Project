package com.fintech.bepc.controller;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.AuthRequestDto;
import com.fintech.bepc.model.dtos.AuthResponseDto;
import com.fintech.bepc.model.dtos.UserRequestDto;
import com.fintech.bepc.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        log.info("User attempting to log in with email: {}", authRequestDto.getEmail());
        AuthResponseDto token = authService.login(authRequestDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("user/register")
    public <T>ResponseEntity<T> registerUser(@Valid @RequestBody UserRequestDto authRequestDto) {
        log.info("User registration attempt with email: {}", authRequestDto.getEmail());
        authService.registerUser(authRequestDto);
        return ResponseEntity.ok((T)ApiResponse.success("Registration successful"));
    }

    @PostMapping("admin/register")
    public <T>ResponseEntity<T> registerAdmin(@Valid @RequestBody UserRequestDto authRequestDto) {
        log.info("User registration attempt with email: {}", authRequestDto.getEmail());
        authService.registerAdmin(authRequestDto);
        return ResponseEntity.ok((T)ApiResponse.success("Registration successful"));
    }
}
