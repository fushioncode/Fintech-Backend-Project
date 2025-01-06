package com.fintech.bepc.services;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.UserRequestDto;
import com.fintech.bepc.model.dtos.UserResponseDto;


public interface UserService {
    ApiResponse<UserResponseDto> getUser(Long id);
    ApiResponse<UserResponseDto> updateUser(Long id, UserRequestDto user);
    void deleteUser(Long id);
    <T>ApiResponse<T> getAllUsers();

    void activeAccount(Long id);
}