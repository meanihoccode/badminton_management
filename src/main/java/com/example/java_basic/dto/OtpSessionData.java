package com.example.java_basic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpSessionData {
    private RegisterRequestDTO userData;
    private String otpCode;
    private LocalDateTime expiryTime;
}