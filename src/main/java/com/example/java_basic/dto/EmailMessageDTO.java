package com.example.java_basic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

        private String to;
    private String fullName;
    private BigDecimal debtAmount;
    private String type; // DEBT_REMINDER or OTP_VERIFY
    private String otpCode;
}