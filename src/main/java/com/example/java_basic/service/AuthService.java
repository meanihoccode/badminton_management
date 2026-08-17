package com.example.java_basic.service;

import com.example.java_basic.dto.LoginRequestDTO;
import com.example.java_basic.dto.RegisterRequestDTO;
import com.example.java_basic.dto.AuthResponseDTO;

public interface AuthService {
    void register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}

