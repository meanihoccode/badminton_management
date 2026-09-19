package com.example.java_basic.controller;

import com.example.java_basic.dto.AuthResponseDTO;
import com.example.java_basic.dto.LoginRequestDTO;
import com.example.java_basic.dto.RegisterRequestDTO;
import com.example.java_basic.dto.OtpVerifyRequestDTO;
import com.example.java_basic.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Giữ lại API cũ cho tương thích (hoặc có thể xóa/deprecate)
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/register/init")
    public ResponseEntity<Map<String, String>> initiateRegistration(@Valid @RequestBody RegisterRequestDTO request) {
        authService.initiateRegistration(request);
        return ResponseEntity.ok(Map.of("message", "Mã OTP đã được gửi đến email của bạn"));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<Map<String, String>> verifyRegistration(@Valid @RequestBody OtpVerifyRequestDTO request) {
        authService.verifyRegistration(request);
        return ResponseEntity.ok(Map.of("message", "Đăng ký tài khoản thành công"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody com.example.java_basic.dto.RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}