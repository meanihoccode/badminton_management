# Tài Liệu: Ứng Dụng Thực Tế Các Chủ Đề Java Core

Tài liệu này giải thích chi tiết cách dự án Badminton Management đã tích hợp và sử dụng các khái niệm cơ bản của Java Core.

---

## 1. Overloading (Nạp chồng phương thức)

**Khái niệm:** Cho phép một Class chứa nhiều hàm cùng tên nhưng khác nhau về danh sách tham số (số lượng, kiểu dữ liệu).

**Áp dụng trong dự án:**
- **Lớp:** `UserService` và `UserServiceImpl`
- **Thực tế:** Tính năng thanh toán tiền công nợ (`payDebt`). Ban đầu chúng ta có một hàm:
  `public UserResponseDTO payDebt(Long userId, BigDecimal amount);`
- Nhưng đôi khi Admin muốn nhập thêm "Ghi chú" (ví dụ: "Tiền sân tháng 5"). Vậy là ta tạo thêm một hàm nữa cùng tên:
  `public UserResponseDTO payDebt(Long userId, BigDecimal amount, String note);`
- Việc này giúp code gọi từ `UserController` trở nên linh hoạt. Nếu request không có `note`, hệ thống tự động gọi hàm 2 tham số. Nếu có `note`, nó sẽ gọi hàm 3 tham số. Hàm 2 tham số thực chất gọi lại hàm 3 tham số với giá trị note mặc định.

---

## 2. Synchronization (Đồng bộ hóa) & StringBuilder

**Khái niệm:** 
- `synchronized`: Là từ khóa khóa (lock) một phương thức, đảm bảo chỉ có 1 Luồng (Thread) duy nhất được quyền chui vào hàm này tại một thời điểm.
- `StringBuilder`: Là công cụ để nối chuỗi cực nhanh và không sinh ra các object rác dư thừa trong bộ nhớ.

**Áp dụng trong dự án:**
- **Lớp:** `InvoiceGenerator`
- **Thực tế:** Khi người dùng thanh toán thành công, hệ thống cần xuất 1 hóa đơn văn bản và cấp Mã hóa đơn duy nhất (VD: `INV-1001`, `INV-1002`).
- Nếu 2 người dùng thanh toán cùng 1 tích tắc, biến `counter++` có thể bị lỗi (Race condition) khiến 2 hóa đơn cùng mang mã `INV-1001`. Do đó ta đặt `public synchronized String generateNextInvoiceId()`. Luồng 2 phải đứng đợi luồng 1 chạy xong mới được vào.
- Sau khi có mã ID, ta dùng `StringBuilder` (append) để nối các đoạn text thành một văn bản Hóa đơn dài sọc. So với việc dùng dấu cộng chuỗi (`+`), StringBuilder nhẹ hơn hàng chục lần vì nó chỉ sửa trên đúng 1 mảng ký tự duy nhất (mutability).

---

## 3. String Pool & Final Class / Final Method

**Khái niệm:**
- **String Pool:** Khi bạn khai báo String bằng cặp ngoặc kép `"ROLE_ADMIN"`, Java sẽ tống nó vào một cái bể (Pool). Ở class khác bạn lại viết `"ROLE_ADMIN"`, Java không tạo ô nhớ mới mà nối cái biến đó về cùng 1 cái bể.
- **Final Class:** Class mang từ khóa `final` cấm không cho class khác `extends` (kế thừa).
- **Final Method:** Hàm mang từ khóa `final` cấm không cho ghi đè (`@Override`).

**Áp dụng trong dự án:**
- **Lớp:** `AppConstants` (Lưu thư mục constant)
- **Thực tế:** Ta khai báo `public final class AppConstants` và đặt constructor thành `private` để cấm khởi tạo. 
- Bên trong chứa: `public static final String ROLE_ADMIN = "ROLE_ADMIN";`
- Khắp nơi trong dự án (như `UserController`, `SecurityConfig`), thay vì gõ chữ `"ROLE_ADMIN"` rải rác dễ bị sai chính tả và khó quản lý, ta dùng chung `AppConstants.ROLE_ADMIN`. 
- Về bản chất, "ROLE_ADMIN" là một chữ (literal) được đưa thẳng vào String Pool giúp tái sử dụng vùng nhớ. Việc đặt class là `final` giúp bảo vệ các hằng số sống còn này không bị "sửa trộm" hoặc phá hoại bởi một class con tà đạo nào đó.
