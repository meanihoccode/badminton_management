package com.example.java_basic.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async("taskExecutor")
    public void sendDebtReminderEmail(String to, String fullName, BigDecimal debtAmount) {
        if (to == null || to.isEmpty()) {
            log.warn("Không có email cho user {}, bỏ qua gửi nhắc nợ.", fullName);
            return;
        }

        try {
            // Chuẩn bị biến cho HTML Template
            Context context = new Context();
            context.setVariable("fullName", fullName);
            
            // Format số tiền theo chuẩn VNĐ
            NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedDebt = format.format(debtAmount.abs()) + " VNĐ";
            context.setVariable("debtAmount", formattedDebt);

            // Sinh HTML từ file debt-reminder.html
            String htmlContent = templateEngine.process("debt-reminder", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("[Nhắc Nhở] Đóng quỹ sân cầu lông");
            helper.setText(htmlContent, true); // true = HTML

            javaMailSender.send(message);
            log.info("Đã gửi email nhắc nợ thành công cho {}", to);
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email nhắc nợ cho {}: {}", to, e.getMessage());
        }
    }
}
