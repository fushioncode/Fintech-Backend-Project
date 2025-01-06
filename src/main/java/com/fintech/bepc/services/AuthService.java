package com.fintech.bepc.services;

import com.fintech.bepc.model.dtos.AuthRequestDto;
import com.fintech.bepc.model.dtos.AuthResponseDto;
import com.fintech.bepc.model.dtos.UserRequestDto;

public interface AuthService {
    AuthResponseDto login(AuthRequestDto authRequestDto);
    void registerAdmin(UserRequestDto authRequestDto);
    void registerUser(UserRequestDto authRequestDto);
}

