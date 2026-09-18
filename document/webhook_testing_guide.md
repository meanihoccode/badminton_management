# Hướng Dẫn Kích Hoạt & Kiểm Thử Webhook Nạp Tiền

Tài liệu này hướng dẫn bạn cách kiểm tra tính năng nạp tiền tự động ngay trên máy tính của mình (Local) và cách kết nối với hệ thống ngân hàng thật (Production).

---

## Phần 1: Test Ngay Lập Tức Bằng Postman (Local)
Vì chúng ta chưa kết nối với ngân hàng, ta sẽ dùng công cụ Postman để "đóng giả" làm máy chủ ngân hàng gửi tin nhắn về Backend.

### Bước 1: Mở giao diện Web của bạn
- Truy cập vào trang Dashboard trên Frontend.
- Bấm **+ Nạp tiền**, ví dụ nhập 200000.
- Chú ý dòng chữ đỏ bên dưới mã QR, nó sẽ ghi là: NAP <Số ID của bạn>. (Ví dụ: NAP 1).
- **Giữ nguyên màn hình này (KHÔNG ĐÓNG)** để chờ tiền nhảy.

### Bước 2: Dùng Postman bắn Webhook
1. Mở Postman, tạo một Request mới.
2. Chọn phương thức: **POST**
3. Nhập URL: http://localhost:8080/api/payments/webhook
4. Chuyển sang tab **Body**, chọn **raw**, đổi kiểu từ Text sang **JSON**.
5. Dán đoạn JSON sau vào:
`json
{
    "transactionId": "GD_MOCK_12345",
    "amount": 200000,
    "description": "NGUYEN VAN A chuyen khoan NAP 1"
}
`
*(Lưu ý: Thay số 1 thành đúng ID của bạn hiển thị trên màn hình Web).*

6. Bấm nút **Send** trên Postman.
7. Ngay lập tức quay lại nhìn màn hình Web, bạn sẽ thấy mã QR tự động biến mất và Số dư nảy thêm 200.000 VND!

*(Nếu bạn bấm Send lần 2 với cùng 	ransactionId là GD_MOCK_12345, Backend sẽ chặn lại nhờ cột external_ref, tiền sẽ không bị cộng đúp).*

---

## Phần 2: Đưa Lên Môi Trường Thật (Tích hợp PayOS / SePay)

Khi dự án của bạn đã sẵn sàng chạy thật cho CLB, bạn sẽ làm theo các bước sau:

### 1. Đăng ký Dịch vụ
- Tạo tài khoản miễn phí tại **PayOS.vn** hoặc **SePay.vn**.
- Liên kết tài khoản Techcombank (121288888999) của bạn vào hệ thống của họ theo hướng dẫn.

### 2. Cấu hình Webhook URL
- Các nền tảng này sẽ có một ô trống tên là **"Webhook URL"**.
- Bạn không thể điền localhost:8080 vì đó là mạng nội bộ của bạn.
- Bạn phải điền tên miền thật của Server bạn thuê (Ví dụ: https://caulongclub.com/api/payments/webhook).

> **Mẹo test Local với Ngrok:** Nếu bạn muốn test ngân hàng thật khi code vẫn đang chạy ở máy tính ở nhà, hãy tải phần mềm **Ngrok**. Chạy lệnh 
grok http 8080, nó sẽ cấp cho bạn một đường link public tạm thời (VD: https://abcd.ngrok-free.app). Lấy link đó điền vào PayOS. Bất cứ ai chuyển khoản tiền thật vào thẻ bạn, tiền trên Localhost sẽ nhảy!

### 3. Điều chỉnh DTO (Nếu cần)
- Cấu trúc JSON chuẩn của chúng ta hiện đang là 	ransactionId, mount, description.
- Khi dùng SePay, cấu trúc JSON của họ có thể hơi khác một chút (Ví dụ họ dùng chữ id thay vì 	ransactionId, dùng chữ content thay vì description).
- Bạn chỉ cần mở file WebhookRequestDTO.java ra và đổi tên biến cho khớp với tài liệu của họ là xong!