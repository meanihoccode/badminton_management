package com.example.java_basic.service;

import com.example.java_basic.dto.LoginRequestDTO;
import com.example.java_basic.dto.RegisterRequestDTO;
import com.example.java_basic.dto.AuthResponseDTO;
import com.example.java_basic.dto.OtpVerifyRequestDTO;

public interface AuthService {
    void register(RegisterRequestDTO request); // Old method, maybe remove later
    void initiateRegistration(RegisterRequestDTO request);
    void verifyRegistration(OtpVerifyRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}