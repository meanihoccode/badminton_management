# Hệ Thống Quản Lý Câu Lạc Bộ Cầu Lông (Badminton Club Management)

Một hệ thống toàn diện hỗ trợ xếp lịch thi đấu, tự động tính toán chi phí và xử lý thanh toán trực tuyến.

<p align="left">
  <img src="https://img.shields.io/badge/Spring_Boot-3.1.5-6DB33F?style=for-the-badge&logo=spring" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/React-18.0-61DAFB?style=for-the-badge&logo=react" alt="React" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql" alt="MySQL" />
  <img src="https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis" alt="Redis" />
</p>

---

## 1. Tổng Quan Dự Án

Hệ thống Quản lý Câu lạc bộ Cầu lông là một ứng dụng Web Full-stack cấp doanh nghiệp được thiết kế để số hóa và tự động hóa các hoạt động của câu lạc bộ thể thao. Hệ thống giúp loại bỏ các thao tác thủ công trong việc điểm danh người chơi, tính toán chi phí sân bãi dựa trên số trận đấu, quản lý công nợ và xử lý thanh toán.

Kiến trúc của dự án tuân thủ nghiêm ngặt các tiêu chuẩn **Clean Code**, **SOLID principles**, và **RESTful API**, sử dụng các bộ khung lập trình (frameworks) hiện đại để đảm bảo khả năng mở rộng, bảo mật và dễ dàng bảo trì.

## 2. Tính Năng Cốt Lõi

### 2.1. Xác Thực và Phân Quyền (Identity & Security)
- **Role-Based Access Control (RBAC):** Phân chia rõ ràng quyền hạn giữa Quản trị viên (ADMIN) và Thành viên (MEMBER).
- **Stateless Authentication:** Xác thực bảo mật sử dụng JWT (JSON Web Token) kết hợp cơ chế Refresh Token.
- **Quy Trình Xác Thực OTP:** Đăng ký tài khoản an toàn thông qua xác thực Email tự động, kết hợp Redis để lưu trữ tạm thời và chống spam.

### 2.2. Xử Lý Trận Đấu và Chi Phí (Match & Fee Processing)
- **Tự Động Chia Tiền Sân:** Thuật toán phân bổ động và tự động tính toán chi phí sân bãi cho từng người tham gia ngay khi phiên đánh kết thúc.
- **Theo Dõi Kết Quả:** Ghi nhận tỷ số, đội hình tham gia (Team A vs Team B) và hiệu số điểm.
- **Bảng Xếp Hạng (Leaderboard):** Thống kê số liệu để xếp hạng các người chơi tích cực nhất.

### 2.3. Tích Hợp Tài Chính & Cổng Thanh Toán
- **Hệ Thống Ví Nội Bộ:** Quản lý số dư cá nhân của từng thành viên. Tự động trừ tiền khi đánh xong hoặc cộng tiền khi nạp.
- **Tích Hợp PayOS & VietQR:** Tự động sinh mã VietQR động thông qua SDK của PayOS để chuyển khoản ngân hàng 24/7.
- **Webhook tự động (Reconciliation):** Lắng nghe Webhook từ ngân hàng để tự động cập nhật số dư vào cơ sở dữ liệu mà không cần sự can thiệp của con người.
- **Báo Cáo Kế Toán:** Ứng dụng thư viện Apache POI để xuất lịch sử dòng tiền ra file `.xlsx` chuẩn định dạng.

### 2.4. Xử Lý Bất Đồng Bộ (Asynchronous Processing)
- **Spring JMS & Artemis:** Tách các tác vụ nặng (như gửi email OTP) sang hàng đợi tin nhắn (Message Queue) của ActiveMQ Artemis, giúp giảm thiểu độ trễ API và tăng tốc độ phản hồi cho người dùng.

---

## 3. Công Nghệ Sử Dụng (Tech Stack)

### Kiến Trúc Backend
- **Nền tảng Cốt lõi (Core Framework):** Java 17, Spring Boot 3.1.x
- **Tương tác Cơ sở dữ liệu (Persistence Layer):** Spring Data JPA, Hibernate ORM
- **Cơ sở dữ liệu Quan hệ (RDBMS):** MySQL 8 (Tối ưu hóa các câu lệnh truy vấn phức tạp cho báo cáo)
- **Hệ thống Lưu trữ Tạm thời (Caching Strategy):** 
  - **Redis:** Quản lý Global Cache, lưu trữ Refresh Token và Session phân tán.
  - **Caffeine Cache:** Xử lý Local Cache tốc độ cao (In-memory) áp dụng cho luồng đếm ngược OTP (`expireAfterWrite`).
- **Hàng Đợi Thông Điệp (Message Broker & Async):** Spring JMS tích hợp Apache ActiveMQ Artemis (Mô hình Embedded) để tách luồng xử lý gửi Email, giảm độ trễ (latency) cho Main Thread.
- **Bảo mật (Security & IAM):** Spring Security kết hợp JSON Web Token (JJWT) thiết lập cơ chế Stateless Authentication phân quyền cấp độ Method.
- **Tích hợp Thanh toán (Payment Gateway):** PayOS SDK (Sinh VietQR động và tiếp nhận Webhook đối soát giao dịch tự động).
- **Trình tạo Tài liệu (Document Generation):** Apache POI (Ghi xuất dữ liệu Dòng tiền ra định dạng `.xlsx` chuẩn kế toán).
- **Giao tiếp Email (SMTP):** Spring Boot Mail kết hợp Thymeleaf Engine để render Template HTML gửi Email chuyên nghiệp.
- **Tiện ích và Sinh mã (Code Utilities):** 
  - **MapStruct:** Trình ánh xạ (Mapper) hiệu năng cao chuyên biệt cho Entity ↔ DTO.
  - **Lombok:** Tự động sinh mã Boilerplate (Getter, Setter, Builder Pattern).
  - **Hibernate Validator:** Kiểm duyệt tính toàn vẹn của dữ liệu đầu vào (Data Validation) tại Controller.

### Kiến Trúc Frontend
- **Thư viện chính:** React 18
- **Điều hướng:** React Router DOM v6
- **Giao tiếp HTTP:** Axios
- **Giao diện:** CSS thuần với phong cách Glassmorphism UI

---

## 4. Kiến Trúc Phần Mềm (System Architecture)

- **Controller Layer:** Tiếp nhận yêu cầu HTTP, kiểm tra (validate) dữ liệu đầu vào và điều hướng.
- **Service Layer:** Xử lý logic nghiệp vụ cốt lõi, hoàn toàn độc lập với các giao thức mạng hay cơ sở dữ liệu.
- **Repository Layer:** Giao tiếp trực tiếp với cơ sở dữ liệu quan hệ thông qua Spring Data JPA.
- **Mapper Layer (MapStruct):** Tách biệt rạch ròi giữa Domain Entities (thực thể Database) và DTOs (đối tượng truyền tải dữ liệu).
- **Event-Driven Components:** Áp dụng JMS Listeners để xử lý các tác vụ nền.

---

## 5. Hướng Dẫn Cài Đặt (Getting Started)

### Yêu Cầu Hệ Thống (Prerequisites)
- **JDK 17** hoặc mới hơn
- **Node.js 18+** & npm
- **MySQL 8** (Port mặc định: 3306)
- **Redis** (Port mặc định: 6379)
- **Ngrok** (Dành cho việc test Webhook thanh toán PayOS trên Localhost)

### 5.1. Khởi Tạo Cơ Sở Dữ Liệu
Tạo một schema mới trong MySQL:
```sql
CREATE DATABASE badminton_db;
```

### 5.2. Cấu Hình Backend
1. Di chuyển vào thư mục gốc của dự án.
2. Tạo file cấu hình từ file mẫu:
   ```bash
   cp application.properties.example src/main/resources/application.properties
   ```
3. Cập nhật `application.properties` với thông tin Database, Secret Key của JWT, Mật khẩu ứng dụng Gmail và API Key của PayOS.
4. Biên dịch và Khởi chạy:
   ```bash
   ./gradlew build -x test
   ./gradlew bootRun
   ```
   *Backend sẽ khởi chạy tại `http://localhost:8080`.*

### 5.3. Cấu Hình Frontend
1. Di chuyển vào thư mục `frontend`:
   ```bash
   cd frontend
   ```
2. Cài đặt các thư viện phụ thuộc:
   ```bash
   npm install
   ```
3. Khởi chạy máy chủ phát triển (Development Server):
   ```bash
   npm start
   ```
   *Frontend sẽ hoạt động tại `http://localhost:3000`.*
