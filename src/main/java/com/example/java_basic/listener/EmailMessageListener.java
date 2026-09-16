package com.example.java_basic.listener;

import com.example.java_basic.dto.EmailMessageDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.NumberFormat;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailMessageListener {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @JmsListener(destination = "emailQueue")
    public void receiveEmailMessage(EmailMessageDTO emailDto) {
        log.info("Nhan duoc yeu cau gui email tu Queue cho: {} - Loai: {}", emailDto.getTo(), emailDto.getType());
        try {
            Context context = new Context();
            context.setVariable("fullName", emailDto.getFullName());
            
            String htmlContent;
            String subject;

            if ("OTP_VERIFY".equals(emailDto.getType())) {
                context.setVariable("otpCode", emailDto.getOtpCode());
                htmlContent = templateEngine.process("otp-email", context);
                subject = "[Mã OTP] Xác thực đăng ký tài khoản";
            } else {
                // Default: DEBT_REMINDER
                NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
                String formattedDebt = format.format(emailDto.getDebtAmount().abs()) + " VND";
                context.setVariable("debtAmount", formattedDebt);
                htmlContent = templateEngine.process("debt-reminder", context);
                subject = "[Nhắc Nhở / Reminder] Đóng quỹ sân cầu lông / Badminton Fund Payment";
            }

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailDto.getTo());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Da gui email ({}) thanh cong qua JMS cho {}", emailDto.getType(), emailDto.getTo());
        } catch (MessagingException e) {
            log.error("Loi khi gui email (JMS) cho {}: {}", emailDto.getTo(), e.getMessage());
        } catch (Exception e) {
            log.error("Loi khong xac dinh khi xu ly email JMS cho {}: {}", emailDto.getTo(), e.getMessage());
        }
    }
}