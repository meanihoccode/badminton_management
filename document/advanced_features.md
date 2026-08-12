# Tài Liệu Chuyên Sâu: Các Tính Năng Nâng Cao (Phase 2)

Tài liệu này giải thích 3 công nghệ cấp độ "Doanh nghiệp" (Enterprise) vừa được áp dụng vào dự án Quản Lý Sân Cầu Lông để tối ưu hóa hiệu năng và mở rộng khả năng quốc tế hóa.

---

## 1. Xử Lý Bất Đồng Bộ Với `@Async` (Tăng tốc độ chạy)

### Vấn đề gặp phải ban đầu
Khi bộ đếm giờ (Scheduler) kích hoạt lúc 8h sáng, nó tìm ra 50 người đang nợ tiền. Hệ thống cũ sẽ gửi email cho người 1 (mất 2s), đợi xong mới gửi người 2 (mất 2s)... Tổng cộng hệ thống bị "treo đơ" 100 giây. Trong lúc đó, nếu người dùng khác truy cập web sẽ thấy trang web tải cực kỳ chậm.

### Giải pháp kỹ thuật
- Dùng **`@EnableAsync`** trong file `AsyncConfig.java` để đánh thức bộ quản lý luồng (Thread Pool) của Spring.
- Khai báo một hồ chứa luồng (ThreadPoolTaskExecutor) với số luồng cốt lõi là 2, tối đa là 5 luồng chạy song song.
- Đặt **`@Async("taskExecutor")`** lên đầu hàm `sendDebtReminderEmail()` trong `EmailService`.

**Kết quả:** 
Hàm `sendDailyDebtReminders()` (Người giao việc) chỉ việc ra lệnh: *"Ê, 50 luồng phụ kia, tụi mày mang 50 lá thư này đi gửi ngay cho tao!"* rồi nó lập tức quay về trạng thái nghỉ (hoàn thành trong 0.1 giây). Việc gửi mail chậm chạp sẽ được các luồng phụ (Background Threads) âm thầm tự làm phía sau hậu trường mà không ảnh hưởng tới luồng chính.

---

## 2. Tiết Kiệm RAM Với Spring Data Projections

### Vấn đề gặp phải ban đầu
Khi Frontend gọi hàm lấy danh sách thành viên để hiển thị lên bảng (Table), Backend đang dùng lệnh `userRepository.findAll()`. Lệnh này sẽ chọc xuống Database và lấy BẰNG SẠCH mọi thông tin (gồm cả mật khẩu mã hóa bcrypt dài loằng ngoằng, quyền hạn, ngày giờ tạo...). Điều này tiêu tốn cực kỳ nhiều RAM của máy chủ vô ích.

### Giải pháp kỹ thuật
- Thay vì dùng Class (Entity), ta tạo ra một chiếc khuôn (Interface) có tên là `UserSummaryProjection`. Chiếc khuôn này chỉ khoét đúng 4 lỗ: `Id`, `FullName`, `Email`, `Balance`, `RacketModel`.
- Viết một hàm mới trong Repository: `List<UserSummaryProjection> findAllProjectedBy()`.
- Spring Data JPA rất thông minh, khi thấy Interface này, nó sẽ tự động sinh ra câu SQL siêu nhẹ: `SELECT id, full_name, email, balance, racket_model FROM users` thay vì `SELECT *`.

**Kết quả:** 
Dữ liệu gửi từ RAM xuống Card mạng giảm đi 50%. Tốc độ tải danh sách nhanh hơn hẳn, không bị rò rỉ dữ liệu nhạy cảm ra bộ nhớ.

---

## 3. Hệ Thống Đa Ngôn Ngữ (I18n - Internationalization)

### Vấn đề gặp phải ban đầu
Dự án cũ gõ cứng (Hardcode) toàn bộ lời nhắn lỗi vào trong code Java, ví dụ: `throw new Exception("Không tìm thấy User")`. Nếu muốn đem bán phần mềm này cho người Mỹ dùng, ta phải tìm hàng trăm file Java để dịch lại từng chữ rất thủ công.

### Giải pháp kỹ thuật
- Tách toàn bộ lời nhắn ra các file từ điển (Properties): `messages_vi.properties` (Tiếng Việt) và `messages_en.properties` (Tiếng Anh).
- Tạo file cấu hình `I18nConfig.java` dùng `AcceptHeaderLocaleResolver`. Công cụ này sẽ nhìn vào Header của Request (do trình duyệt gửi lên) để biết người dùng đang cài máy tính tiếng gì.
- Tiêm (Inject) `MessageSource` vào `UserService`.

**Kết quả:**
Thay vì ném lỗi tiếng Việt, code sẽ viết là:
```java
String msg = messageSource.getMessage("error.user.not.found", new Object[]{id}, LocaleContextHolder.getLocale());
throw new ResourceNotFoundException(msg);
```
Nếu một người Mỹ (hoặc Postman chỉnh Header `Accept-Language: en`) gọi API lấy User bị sai ID, họ sẽ nhận được câu lỗi cực chuẩn: *"User with ID 99 not found"* thay vì tiếng Việt! Hàng ngàn câu chữ trên toàn hệ thống giờ đây được quản lý tập trung ở đúng một chỗ.
