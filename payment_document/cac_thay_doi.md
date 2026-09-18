# Các Thay Đổi Về Mã Nguồn (Changelog)

Tài liệu này liệt kê chi tiết các thay đổi trong mã nguồn để chuyển từ phương thức Polling cũ (VietQR trơn) sang hệ thống thanh toán tự động qua PayOS SDK.

## 1. Cấu hình Backend (Dependencies & Properties)
- **`build.gradle`**: 
  - Thêm thư viện `implementation 'vn.payos:payos-java:2.0.1'` để sử dụng SDK chính thức của PayOS.
- **`application.properties`**:
  - Thêm 3 tham số cấu hình: `client.id`, `api.key`, `checksum.key` được cấp từ bảng điều khiển của PayOS.

## 2. Các Lớp Cấu Hình & Entity
- **`com.example.java_basic.config.PayOSConfig`** (MỚI):
  - Khởi tạo Spring Bean `PayOS` để sử dụng toàn cục trong ứng dụng. Bean này đọc tự động các giá trị cấu hình từ `application.properties`.
- **`com.example.java_basic.entity.User`**:
  - Sửa lỗi cảnh báo của thư viện Lombok bằng cách thêm `@Builder.Default` cho thuộc tính `balance = BigDecimal.ZERO`.
- **`com.example.java_basic.security.SecurityConfig`**:
  - Đưa `/api/payments/webhook` vào danh sách ngoại lệ `permitAll()` để máy chủ PayOS có thể gọi tới mà không cần truyền JWT Token.

## 3. Lớp Dịch Vụ (Service)
- **`com.example.java_basic.service.PaymentService`** & **`PaymentServiceImpl`**:
  - Xóa bỏ các logic xử lý cũ (như hàm xác nhận nạp tiền thủ công từ Admin).
  - Thêm hàm `createPaymentLink`: Sử dụng `CreatePaymentLinkRequest` để sinh ra link checkout của PayOS. Khắc phục lỗi tương thích kiểu dữ liệu (chuyển `quantity` từ `1L` về `1` theo đúng tài liệu SDK).
  - Thêm hàm `processWebhook`: 
    - Dùng `payOS.webhooks().verify(webhookBody)` để xác thực tính toàn vẹn của dữ liệu (ngăn ngừa hacker gọi fake webhook).
    - Tách `userId` từ chuỗi `description` của giao dịch.
    - Cập nhật số dư (`balance`) cho User tương ứng và lưu lại bản ghi `Transaction` vào cơ sở dữ liệu.
    - Đổi kiểu tham số từ `ObjectNode` sang `vn.payos.model.webhooks.Webhook` để giải quyết triệt để lỗi ép kiểu của Jackson Library.

## 4. Lớp Điều Khiển (Controller)
- **`com.example.java_basic.controller.PaymentController`**:
  - Tạo endpoint `POST /api/payments/create`: Nhận yêu cầu từ người dùng (kèm JWT Token). Lấy `username` từ JWT, tra cứu ra `userId` và sinh link thanh toán. (Đã sửa lỗi `NumberFormatException` do lúc đầu ép kiểu trực tiếp `username` sang `Long`).
  - Tạo endpoint `POST /api/payments/webhook`: Mở cửa để nhận các HTTP POST request từ PayOS, truyền dữ liệu thẳng xuống Service để cộng tiền.

## 5. Giao Diện (Frontend)
- **`frontend/src/components/Dashboard.jsx`**:
  - Gỡ bỏ hoàn toàn logic Polling (JS `setInterval` liên tục gọi API mỗi 3 giây).
  - Thay ảnh mã VietQR tĩnh thành một nút bấm gọi hàm `api.post('/api/payments/create')`.
  - Nhận `checkoutUrl` từ Backend và chuyển hướng người dùng bằng `window.location.href = res.data.checkoutUrl`.
  - Cập nhật logic khi người dùng quay trở lại: Bắt các tham số `?code=00&status=PAID` trên thanh địa chỉ.
  - Hiển thị "Success Modal" báo nạp tiền thành công và thêm sự kiện `window.location.reload()` vào nút "Tuyệt vời" để giao diện tự cập nhật số dư mới nhất.
