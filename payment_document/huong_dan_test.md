# Hướng Dẫn Test Tính Năng Thanh Toán PayOS

Tài liệu này hướng dẫn các bước kiểm thử từ đầu đến cuối (End-to-End Test) luồng thanh toán tích hợp với PayOS khi phát triển dưới Localhost.

## Công Cụ Chuẩn Bị
1. Backend Spring Boot (đang chạy trên cổng 8080).
2. Frontend Vite/React (đang chạy trên cổng 5173).
3. **Ngrok**: Để Expose cổng 8080 ra Internet (giúp PayOS có thể gửi Webhook về máy tính của bạn).

---

## Bước 1: Khởi động Ngrok và Cập nhật Webhook
Vì PayOS không thể gọi trực tiếp vào `localhost:8080` của bạn, bạn phải dùng Ngrok.
1. Mở Terminal (PowerShell hoặc CMD), chạy lệnh:
   ```bash
   ngrok http 8080
   ```
2. Ngrok sẽ sinh ra một đường dẫn dạng: `https://<random-id>.ngrok-free.dev`.
3. Đăng nhập vào trang quản trị của PayOS (https://my.payos.vn).
4. Vào phần **Cấu hình kênh thanh toán**, dán đường link Webhook theo định dạng:
   `https://<random-id>.ngrok-free.dev/api/payments/webhook`
5. Bấm Xác nhận / Lưu cấu hình.

*(Lưu ý: Nếu bạn tắt Ngrok và bật lại, bạn sẽ được cấp một URL mới, lúc đó bạn phải lặp lại bước 3 & 4).*

---

## Bước 2: Test Luồng Frontend (Checkout URL)
1. Truy cập vào trang Frontend `http://localhost:5173`.
2. Đăng nhập bằng tài khoản thành viên.
3. Bấm vào nút **Nạp Tiền** trên Dashboard.
4. Nhập số tiền (ví dụ: `10000`) và bấm **Thanh Toán Qua PayOS**.
5. **Kỳ vọng:** Bạn sẽ được chuyển hướng sang giao diện quét mã QR của PayOS. URL trên thanh địa chỉ phải đổi sang tên miền của PayOS.

---

## Bước 3: Test Thanh Toán Thực (Hoặc Giả Lập)
1. Tại trang Checkout của PayOS, bạn có thể lấy điện thoại quét mã QR và chuyển khoản thật bằng App Ngân Hàng. 
2. (Tùy chọn) Nếu tài khoản PayOS của bạn đang ở chế độ Test, bạn sẽ thấy một nút "Mô phỏng thanh toán thành công". Hãy bấm vào đó để không tốn tiền thật.
3. **Kỳ vọng:** 
   - Ngay sau khi chuyển khoản, trang PayOS sẽ báo thành công và đếm ngược để tự động nhảy về trang `http://localhost:5173/`.
   - Khi nhảy về, giao diện web của bạn sẽ bung lên một thông báo **Thành Công!**.

---

## Bước 4: Test Luồng Webhook & Backend
1. Sau khi xem thông báo thành công, hãy bấm vào nút **"Tuyệt vời"** trên màn hình.
2. Trang web sẽ tự động Tải lại (Reload).
3. **Kỳ vọng trên Giao diện:** Số dư (Balance) của bạn đã được cộng thêm 10,000 VND.
4. **Kỳ vọng trên Backend:** 
   - Mở cửa sổ Terminal đang chạy Spring Boot, bạn sẽ thấy log in ra:
     `Nhan webhook thanh toan tu PayOS: ...`
     `Da cong 10000 VND cho user [tên_user] thanh cong tu webhook PayOS.`
   - Trong Database, bảng `users` được cập nhật cột `balance`. Bảng `transactions` sinh ra một dòng log (DEPOSIT).

## Các Lưu Ý Khi Debug (Bắt Bệnh)
- **Tiền không lên:** 
  - Xem màn hình Ngrok có dòng `POST /api/payments/webhook 200 OK` hay không. 
  - Nếu hiện `502 Bad Gateway`: Spring Boot của bạn bị tắt hoặc crash.
  - Nếu hiện `403 Forbidden` hoặc `401 Unauthorized`: Config Security của bạn đang block đường dẫn Webhook.
  - Nếu hiện `500 Internal Server Error`: Backend bị lỗi Code (sai kiểu dữ liệu, lưu DB lỗi,...). Hãy đọc log console của Spring Boot.
- **PayOS báo Lỗi tạo mã thanh toán:**
  - Kiểm tra xem 3 key (Client ID, API Key, Checksum Key) trong `application.properties` đã nhập đúng chưa.
  - Kiểm tra log Backend xem có lỗi `NumberFormatException` hoặc tham số bị thiếu không.
