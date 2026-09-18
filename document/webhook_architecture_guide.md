# Kiến Trúc Webhook & Hướng Dẫn Tích Hợp (Payment)

Tài liệu này giải thích bản chất kỹ thuật của Webhook, cách đoạn code Spring Boot của chúng ta hoạt động, và quy trình đưa lên môi trường thật (Production).

---

## 1. Bản chất Webhook là gì?
Trái ngược với API thông thường (Frontend chủ động đi hỏi Backend), **Webhook là "API đảo ngược" (Reverse API)**.
- Khi có một sự kiện xảy ra (Vd: có người chuyển 200k vào tài khoản Techcombank của bạn), máy chủ của Ngân hàng / Cổng thanh toán (như PayOS, SePay) sẽ **chủ động** gửi một gói tin HTTP POST tới một địa chỉ (URL) do bạn cung cấp.
- URL đó chính là Webhook của hệ thống chúng ta: POST /api/payments/webhook.

## 2. Phân tích Code Backend của chúng ta

### A. Mở cửa bảo mật (SecurityConfig)
Mặc định Spring Security chặn mọi Request không có JWT Token. Nhưng máy chủ ngân hàng lại không có Token của user.
=> Giải pháp: Thêm .requestMatchers("/api/payments/webhook").permitAll() để mở toang cánh cửa này cho bên thứ 3 gọi vào.

### B. Controller (Người đón khách)
`java
@PostMapping("/webhook")
public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody WebhookRequestDTO request)
`
Nhiệm vụ của PaymentController chỉ đơn giản là mở cửa đón gói tin (JSON) từ Ngân hàng, ép kiểu thành đối tượng WebhookRequestDTO, và ném sang cho Service xử lý. Cuối cùng, trả về HTTP 200 OK để báo cho Ngân hàng biết: "Tôi đã nhận được tin báo rồi nhé".

### C. Service (Khối óc xử lý & Chống gian lận)
Trong PaymentServiceImpl, luồng xử lý trải qua 3 lớp:

1. **Lớp khiên (Idempotent - Chống nạp đúp):**
   `java
   if (transactionRepository.existsByExternalRef(request.getTransactionId())) { return; }
   `
   Ngân hàng đôi khi bị lỗi mạng, họ có thể gửi cùng 1 thông báo giao dịch (cùng 	ransactionId) đến 2-3 lần. Câu lệnh này giúp ta đảm bảo 1 giao dịch chỉ được cộng tiền 1 lần.

2. **Lớp thợ mỏ (Regex - Khai thác ID):**
   `java
   Pattern pattern = Pattern.compile("NAP\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
   Matcher matcher = pattern.matcher(description);
   `
   Dù người dùng chuyển khoản có ghi sai dấu, viết hoa viết thường (VD: "Nguyen Van A nap 15 the", "nap  15"), Regex sẽ luôn quét trúng chữ NAP và lấy ra được con số 15 (User ID).

3. **Lớp thủ quỹ (Cộng tiền):**
   Nếu tìm thấy User ID = 15 trong Database, hệ thống tăng alance và tạo một Transaction (lịch sử giao dịch) loại DEPOSIT kèm theo việc lưu externalRef để khóa không cho mã giao dịch này được dùng lại.

---

## 3. Hướng Dẫn Tích Hợp Vào Thực Tế (Production)

Hiện tại bạn đang chạy Code ở localhost:8080, máy chủ PayOS/SePay trên Internet không thể nhìn thấy máy của bạn.

### Bước 1: Expose Localhost ra Internet (Dùng Ngrok)
Khi bạn đang code ở nhà nhưng vẫn muốn test nạp tiền thật:
1. Tải và cài đặt phần mềm **Ngrok**.
2. Mở Terminal gõ lệnh: 
grok http 8080
3. Ngrok sẽ sinh ra một đường link, VD: https://8a2b-12-34.ngrok.app. Đường link này thông thẳng vào localhost:8080 của bạn.

### Bước 2: Cấu hình trên SePay hoặc PayOS
1. Tạo tài khoản tại https://my.sepay.vn/ hoặc https://payos.vn/.
2. Liên kết tài khoản Techcombank thật của bạn vào.
3. Tìm mục **Cấu hình Webhook** trên web của họ.
4. Dán đường link API của bạn vào, dạng: https://8a2b-12-34.ngrok.app/api/payments/webhook.
5. Bấm Lưu.

### Bước 3: Đồng bộ biến (DTO Mapping)
Mỗi bên có một cách đặt tên biến JSON khác nhau. Hiện DTO của chúng ta đang có: 	ransactionId, mount, description.
- Ví dụ **PayOS** trả về: mount, description, và id (thay vì 	ransactionId).
- Bạn chỉ việc vào file WebhookRequestDTO.java đổi biến 	ransactionId thành id (hoặc dùng @JsonProperty("id")) là xong.

Từ giây phút này, bất kỳ ai lấy điện thoại chuyển khoản thật vào ngân hàng Techcombank của bạn với nội dung NAP <ID>, tiền ảo trên Website sẽ tự động nảy số!