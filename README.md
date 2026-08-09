# Badminton Club Management System (Hệ Thống Quản Lý Sân Cầu Lông)

Dự án này là một ứng dụng Web Fullstack hoàn chỉnh giúp các câu lạc bộ (hoặc nhóm) cầu lông dễ dàng quản lý thành viên, lên lịch buổi đánh, ghi nhận điểm số từng set đấu và đặc biệt là tự động hóa việc tính toán chia tiền sân/tiền cầu cực kỳ minh bạch và chính xác.

Được xây dựng như một đồ án mẫu áp dụng toàn bộ các kiến thức từ giáo trình Lập Trình Web Doanh Nghiệp (Java Spring Boot & React).

---

## Các Tính Năng Nổi Bật

- **Xác Thực & Phân Quyền (JWT + Spring Security)**
  - Tách biệt rõ ràng 2 quyền: ADMIN (Chủ sân) và MEMBER (Thành viên).
  - Admin có toàn quyền tạo buổi đánh, nhập điểm, thêm/sửa/xóa người chơi và chốt sổ chia tiền.
  - Member chỉ được phép xem lịch sử đánh và lịch sử giao dịch cá nhân.
- **Quản Lý Buổi Đánh (Sessions) & Ghi Điểm**
  - Quản lý danh sách các buổi đánh theo ngày và sân.
  - Ghi nhận chi tiết từng trận (Game Match) diễn ra trong buổi: Chọn cặp đấu 2 vs 2, ghi nhận điểm số, tự động phân định thắng/thua.
  - Đội thắng được cộng tiền thưởng (mặc định 5k), đội thua bị trừ tiền ngay lập tức vào ví ảo.
- **Chốt Sổ & Chia Tiền Tự Động**
  - Nhập tổng tiền thuê sân và tiền cầu của một buổi đánh.
  - Hệ thống tự động đếm số lượng người tham gia thực tế ngày hôm đó và chia đều tiền, trừ thẳng vào ví ảo của từng người.
  - Ngăn chặn chốt sổ nhiều lần (Double-charging) bằng bẫy lỗi an toàn.
- **Tự Động Nhắc Nợ Bằng Email (Spring Scheduler & Thymeleaf)**
  - Hệ thống "kế toán máy" thức dậy vào 08:00 sáng mỗi ngày.
  - Quét tìm tất cả thành viên có số dư âm (đang nợ tiền) và tự động gửi Email nhắc nợ HTML màu sắc đỏ rực để nhắc nhở nộp quỹ.
- **Tối Ưu Code Bằng MapStruct & Lombok**
  - Tự động hóa quá trình ánh xạ dữ liệu (Entity <-> DTO).
- **An Toàn Tuyệt Đối (Unit Test & Mockito)**
  - Thuật toán tính tiền và chia tiền được bảo vệ bởi bộ Unit Test tự động, đảm bảo không một ai có thể bị tính toán sai sót.

---

## Công Nghệ Sử Dụng

### Backend
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA / Hibernate** (Tương tác Database)
- **Spring Security & JJWT** (Bảo mật & Cấp vé Token)
- **Spring Boot Mail & Thymeleaf** (Thiết kế & Gửi Email)
- **MapStruct & Lombok** (Tối ưu mã nguồn)
- **JUnit 5 & Mockito** (Kiểm thử)
- **MySQL** (Hệ Quản Trị CSDL)

### Frontend
- **React 18** (Vite)
- **Tailwind CSS** (Thiết kế giao diện đẹp mắt, Responsive)
- **React Router** (Điều hướng trang)
- **Axios** (Giao tiếp API với Backend)

---

## Hướng Dẫn Cài Đặt (Local Development)

### 1. Yêu cầu môi trường
- JDK 17+
- Node.js 18+
- MySQL Server

### 2. Thiết lập Backend (Spring Boot)
1. Tạo một cơ sở dữ liệu mới trong MySQL (Ví dụ: badminton_db).
2. Mở thư mục dự án Java bằng IntelliJ IDEA hoặc Eclipse.
3. Nhân bản (Copy) file src/main/resources/application-example.properties và đổi tên thành application.properties.
4. Điền các thông tin kết nối DB, JWT Secret Key và cấu hình Gmail App Password của bạn vào file application.properties vừa tạo.
5. Chạy ứng dụng bằng lệnh:
   ```bash
   ./gradlew bootRun
   ```

### 3. Thiết lập Frontend (React)
1. Mở một cửa sổ Terminal khác, di chuyển vào thư mục frontend:
   ```bash
   cd frontend
   ```
2. Cài đặt các thư viện cần thiết:
   ```bash
   npm install
   ```
3. Chạy Server phát triển giao diện:
   ```bash
   npm run dev
   ```
4. Mở trình duyệt và truy cập vào đường dẫn http://localhost:5173.

---

## Tài Liệu Kỹ Thuật
Nếu bạn muốn tìm hiểu sâu hơn về kiến trúc ngầm của dự án, vui lòng đọc các tài liệu phân tích mã nguồn chi tiết trong thư mục document/:
- [Tổng kết Giáo trình NCC](document/ncc_syllabus_mapping.md)
- [Bảo Mật & Cơ chế JWT](document/security_jwt.md)
- [Scheduler & Gửi Email Tự Động](document/email_service.md)
- [Từ Điển Các Spring Annotations](document/spring_annotations.md)
