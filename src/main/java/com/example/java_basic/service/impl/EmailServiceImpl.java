package com.example.java_basic.service.impl;
import com.example.java_basic.service.*;
import com.example.java_basic.dto.EmailMessageDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JmsTemplate jmsTemplate;

    @Override
    public void sendDebtReminderEmail(String to, String fullName, BigDecimal debtAmount) {
        if (to == null || to.isEmpty()) {
            log.warn("Khong co email cho user {}, bo qua gui nhac no.", fullName);
            return;
        }

        EmailMessageDTO message = EmailMessageDTO.builder()
                .to(to)
                .fullName(fullName)
                .debtAmount(debtAmount)
                .type("DEBT_REMINDER")
                .build();

        jmsTemplate.convertAndSend("emailQueue", message);
        log.info("Da day yeu cau gui email vao Queue cho {}", to);
    }
    
    @Override
    public void sendOtpEmail(String to, String fullName, String otpCode) {
        EmailMessageDTO message = EmailMessageDTO.builder()
                .to(to)
                .fullName(fullName)
                .type("OTP_VERIFY")
                .otpCode(otpCode)
                .build();
        jmsTemplate.convertAndSend("emailQueue", message);
        log.info("Da day yeu cau gui OTP vao Queue cho {}", to);
    }
}