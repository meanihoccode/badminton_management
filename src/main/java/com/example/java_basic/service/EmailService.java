package com.example.java_basic.service;

import java.math.BigDecimal;

public interface EmailService {
    void sendDebtReminderEmail(String to, String fullName, BigDecimal debtAmount);

    void sendOtpEmail(String to, String fullName, String otpCode);
}
