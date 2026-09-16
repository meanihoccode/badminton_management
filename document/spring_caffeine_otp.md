# Hệ Thống Đăng Ký Xác Thực OTP Bằng Caffeine Cache & JMS

Tài liệu này giải thích chi tiết luồng đăng ký tài khoản 2 bước (Xác thực qua OTP Email) được triển khai trong dự án, đặc biệt tập trung vào cơ chế **In-memory Cache (Caffeine)**.

## 1. Tại sao lại dùng Cache (Caffeine) mà không lưu OTP vào Database?
Khi người dùng đăng ký, ta cần sinh ra một mã OTP 6 số và chờ họ nhập lại. 
- **Nếu lưu vào Database (MySQL):** Mỗi lần người dùng gửi yêu cầu, ta lại phải INSERT một bản ghi vào bảng otp_tokens. Nếu họ không bao giờ nhập OTP (do spam, bot, đổi ý), bảng này sẽ chứa đầy dữ liệu rác, gây phình to Database và phải tốn công viết các Job (lịch trình) dọn dẹp mỗi ngày.
- **Nếu dùng Caffeine Cache (In-memory):** Dữ liệu OTP và thông tin đăng ký tạm thời được lưu thẳng vào thanh RAM của Server. Caffeine tự động có cơ chế hẹn giờ (Time-To-Live - TTL). Đúng 5 phút sau, Caffeine tự động xóa sạch dữ liệu này khỏi RAM, không để lại bất kỳ rác nào trong hệ thống. Hơn nữa, tốc độ đọc/ghi trên RAM luôn nhanh hơn Database gấp hàng ngàn lần.

## 2. Kiến trúc Cache trong dự án
Chúng ta sử dụng thư viện com.github.ben-manes.caffeine:caffeine được tích hợp sẵn qua module spring-boot-starter-cache.

### 2.1. Cấu hình Caffeine Cache
- **File:** src/main/java/com/example/java_basic/config/CacheConfig.java
- **Vai trò:** Khởi tạo bộ đệm mang tên otpCache. Giới hạn dung lượng tối đa là 10.000 bản ghi (tránh tràn RAM nếu bị Bot DDoS). Đặt thời gian hết hạn (expireAfterWrite) là 5 phút.

### 2.2. DTO Lưu Trữ Tạm
- **File:** src/main/java/com/example/java_basic/dto/OtpSessionData.java
- **Vai trò:** Khi lưu vào RAM, ta lưu nguyên một cục Object gồm: userData (thông tin người dùng điền trên form), otpCode (mã 6 số), và expiryTime. Nhờ đó ta không cần lưu thông tin người dùng vào bảng users cho đến khi họ thực sự xác thực thành công.

## 3. Luồng hoạt động chi tiết (Flow)

### Bước 1: Khởi tạo Đăng ký (Init)
- **API:** POST /api/auth/register/init
- **Controller:** src/main/java/com/example/java_basic/controller/AuthController.java
- **Service:** src/main/java/com/example/java_basic/service/impl/AuthServiceImpl.java (Hàm initiateRegistration)
- **Logic:**
  1. Frontend gửi RegisterRequestDTO.
  2. Backend kiểm tra xem username hoặc email đã tồn tại trong Database chưa. Nếu có -> Ném lỗi.
  3. Backend random mã OTP 6 số.
  4. Đưa toàn bộ DTO và OTP vào Caffeine Cache (cache.put(email, sessionData)).
  5. Đẩy lệnh gửi Email vào hàng đợi **JMS ActiveMQ**.

### Bước 2: Bắn Email không đồng bộ (JMS)
- **DTO:** src/main/java/com/example/java_basic/dto/EmailMessageDTO.java (Thêm trường 	ype = "OTP_VERIFY" và otpCode).
- **Producer:** src/main/java/com/example/java_basic/service/impl/EmailServiceImpl.java (Hàm sendOtpEmail dùng JmsTemplate.convertAndSend).
- **Consumer:** src/main/java/com/example/java_basic/listener/EmailMessageListener.java
- **Giao diện Email:** src/main/resources/templates/otp-email.html
- **Logic:** Worker ngầm lắng nghe Queue, nhận được tin nhắn sẽ dùng Thymeleaf ghép OTP vào khung HTML đẹp mắt và gửi tới người dùng mà không làm nghẽn luồng đăng ký của API.

### Bước 3: Xác thực OTP (Verify) & Lưu Database
- **API:** POST /api/auth/register/verify
- **Controller:** src/main/java/com/example/java_basic/controller/AuthController.java
- **Service:** src/main/java/com/example/java_basic/service/impl/AuthServiceImpl.java (Hàm erifyRegistration)
- **Logic:**
  1. Frontend gửi email và otp người dùng nhập.
  2. Backend gọi cache.get(email). Nếu null -> Lỗi (Quá hạn hoặc sai email).
  3. So sánh OTP do người dùng nhập với OTP trong Cache. Nếu sai -> Lỗi.
  4. Nếu mọi thứ OK: Lấy userData từ trong Cache ra, băm mật khẩu (Bcrypt) và thực sự gọi userRepository.save(user) xuống MySQL Database.
  5. Xóa Cache ngay lập tức (cache.evict(email)) để mã OTP không thể dùng lại lần thứ 2.

## 4. Frontend ReactJS
- **File:** rontend/src/components/Register.jsx
- Giao diện được thiết kế 2 State (step === 1 và step === 2). Xử lý khéo léo bóc tách các lỗi 400 Bad Request từ Spring Boot trả về để người dùng biết chính xác họ điền sai cái gì.