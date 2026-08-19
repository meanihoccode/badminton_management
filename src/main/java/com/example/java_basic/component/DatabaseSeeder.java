package com.example.java_basic.component;

import com.example.java_basic.entity.User;
import com.example.java_basic.enums.Role;
import com.example.java_basic.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Minh họa Bean Lifecycle: @PostConstruct và @PreDestroy.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("--- [Bean Lifecycle] @PostConstruct: DatabaseSeeder khởi tạo ---");
        // Kiểm tra xem đã có admin nào chưa
        long adminCount = userRepository.count(); // Giả lập đếm xem db có rỗng không
        if (adminCount == 0) {
            log.info("Chưa có User nào trong CSDL. Tiến hành tạo tài khoản Admin mặc định...");
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("System Admin")
                    .email("admin@badminton.com")
                    .role(Role.ADMIN)
                    .balance(BigDecimal.ZERO)
                    .build();
            userRepository.save(admin);
            log.info("Đã tạo thành công tài khoản: admin / 123456");
        } else {
            log.info("CSDL đã có dữ liệu. Bỏ qua Seeding.");
        }
    }

    @PreDestroy
    public void cleanup() {
        log.info("--- [Bean Lifecycle] @PreDestroy: DatabaseSeeder chuẩn bị bị tiêu hủy do ứng dụng tắt ---");
        // Ở đây có thể viết code dọn dẹp bộ nhớ đệm (clear cache), ngắt kết nối,...
    }
}
