# Tài Liệu Chuyên Sâu: Lập Lịch Tự Động (Scheduler) & Gửi Email (Thymeleaf)

Tài liệu này bóc tách mã nguồn (source code) để giải thích cách hệ thống tự động hóa công việc đòi nợ, giảm tải gánh nặng cho người quản lý sân.

## 1. Kích Hoạt Bộ Đếm Giờ (Spring Scheduler)

Để Spring Boot chạy nền một vòng lặp thời gian, ta cần 2 bước:
1. Gắn `@EnableScheduling` trên class `JavaBasicApplication.java` (Class chứa hàm `main`).
2. Gắn `@Scheduled` trên phương thức muốn nó tự chạy.

### Phân tích file `DebtReminderJob.java`:
```java
@Component // Khai báo đây là một hạt đậu (Bean) để Spring quản lý
@RequiredArgsConstructor
public class DebtReminderJob {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // Cron expression: Giây - Phút - Giờ - Ngày - Tháng - Ngày trong tuần
    // "0 0 8 * * *" = Chạy vào đúng 08:00:00 sáng, mọi ngày, mọi tháng
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyDebtReminders() {
        // 1. Quét DB tìm con nợ (Query này nằm trong UserRepository)
        // @Query("SELECT u FROM User u WHERE u.balance < 0")
        List<User> usersInDebt = userRepository.findUsersInDebt();

        // 2. Với từng người, đẩy qua EmailService
        for (User user : usersInDebt) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendDebtReminderEmail(
                    user.getEmail(), 
                    user.getFullName(), 
                    user.getBalance().abs() // Lấy giá trị tuyệt đối (đang âm 50k thành số 50k)
                );
            }
        }
    }
}
```

## 2. Gửi Email Bằng HTML (Thymeleaf + JavaMailSender)

Chúng ta không gửi thư bằng chữ trắng đen nhàm chán (`SimpleMailMessage`). Chúng ta gửi một bức thư có thiết kế HTML (`MimeMessage`) nhờ sự trợ giúp của Thymeleaf.

### 2.1. Cấu trúc thư mục Thymeleaf
Mẫu email được đặt cố định tại `src/main/resources/templates/debt-reminder.html`. Trong file này, ta dùng biến động:
```html
<p>Chào <strong><span th:text="${name}">Người dùng</span></strong>,</p>
<p>Số tiền cước phí cầu lông của bạn hiện đang nợ là: 
   <strong style="color: red;" th:text="${amount} + ' VNĐ'">0 VNĐ</strong>
</p>
```

### 2.2. Phân tích `EmailService.java`
```java
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender; // Công cụ kết nối tới Google SMTP
    private final TemplateEngine templateEngine; // Công cụ Thymeleaf để vẽ HTML

    public void sendDebtReminderEmail(String toEmail, String name, BigDecimal amount) {
        try {
            // 1. Nhúng dữ liệu thật vào bản vẽ HTML
            Context context = new Context();
            context.setVariable("name", name); // Gắn tên
            context.setVariable("amount", amount); // Gắn số tiền
            String htmlContent = templateEngine.process("debt-reminder", context);

            // 2. Tạo bức thư có định dạng Mime (Cho phép chứa mã HTML)
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail); // Người nhận
            helper.setSubject("[Sân Cầu Lông] - Thông báo nhắc nợ cước phí"); // Tiêu đề thư
            helper.setText(htmlContent, true); // true = Bật chế độ dịch mã HTML

            // 3. Phóng thư đi!
            mailSender.send(message);

        } catch (MessagingException e) {
            // Nếu mất mạng hoặc Google chặn, lỗi sẽ văng ra đây
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
```

## 3. Cấu Hình Ứng Dụng (Gmail SMTP)
Để `JavaMailSender` biết phải đăng nhập vào đâu, file `application.properties` cần có:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tendangnhap@gmail.com
spring.mail.password=khong_phai_mat_khau_chinh
# Phải bật xác thực TLS để gửi thư mã hóa an toàn qua port 587
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
**Mật khẩu ứng dụng (App Password):** Đây là chìa khóa 16 ký tự do Google sinh ra sau khi bạn bật xác minh 2 bước. Nó cho phép ứng dụng Java của bạn thay mặt bạn gửi thư mà không sợ bị lộ mật khẩu chính của tài khoản Google.
