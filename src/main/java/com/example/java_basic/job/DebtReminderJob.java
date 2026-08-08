package com.example.java_basic.job;

import com.example.java_basic.entity.User;
import com.example.java_basic.repository.UserRepository;
import com.example.java_basic.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebtReminderJob {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // Chạy mỗi 1 phút để test dễ dàng. Thực tế thường dùng: "0 0 8 * * MON" (8h sáng thứ Hai)
    @Scheduled(fixedRate = 60000)
    public void scanAndSendDebtReminders() {
        log.info("Bắt đầu quét danh sách thành viên nợ quỹ...");
        List<User> debtors = userRepository.findUsersInDebt();

        if (debtors.isEmpty()) {
            log.info("Không có ai nợ quỹ. Tuyệt vời!");
            return;
        }

        for (User user : debtors) {
            log.info("Phát hiện {} nợ {} VNĐ", user.getFullName(), user.getBalance());
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendDebtReminderEmail(user.getEmail(), user.getFullName(), user.getBalance());
            } else {
                log.warn("User {} không có địa chỉ email, bỏ qua gửi thư.", user.getFullName());
            }
        }
        
        log.info("Hoàn tất quét nợ quỹ.");
    }
}
