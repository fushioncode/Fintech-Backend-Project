package com.fintech.bepc.controller;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.UserRequestDto;
import com.fintech.bepc.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get User by ID",
            description = "Retrieve user details by their ID",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping("activate/{id}")
    @Operation(
            summary = "Active user by ID",
            description = "Activate user account by user ID",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> activateUser(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        userService.activeAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account activated successfully"));
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update user by ID",
            description = "Update user details by their ID",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto user) {
        log.info("Updating user with id: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("delete/{id}")
    @Operation(
            summary = "Delete user by ID",
            description = "Delete user details by their ID",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all-users")
    @Operation(
            summary = "Get all user",
            description = "Retrieve details of all user on the system",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> getAllUsers() {
        log.info("Fetching all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
