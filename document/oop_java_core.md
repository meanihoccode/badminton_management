# 📚 Tài Liệu Ôn Tập: OOP (Java Core) trong Thực Tế Dự Án

Một dự án "chuẩn doanh nghiệp" (Enterprise) viết bằng Java / Spring Boot không bao giờ chỉ dùng các Class cơ bản (Concrete Class). Người ta luôn tìm cách áp dụng triệt để 4 tính chất của OOP (Đóng gói, Kế thừa, Đa hình, Trừu tượng) thông qua **Interface** và **Abstract Class**.

Dưới đây là cách chúng ta vừa nâng cấp kiến trúc mã nguồn của dự án Quản Lý Sân Cầu Lông để áp dụng hoàn hảo 2 khái niệm này. Đây là những kiến thức "ăn điểm tuyệt đối" khi đi bảo vệ đồ án hoặc phỏng vấn.

---

## 1. Abstract Class (Lớp Trừu Tượng) & Kế Thừa (Inheritance)

### 🚨 Vấn đề hệ thống cũ gặp phải
Trong Database của chúng ta có 4 bảng: `users`, `transactions`, `badminton_sessions`, `match_participants`. Cả 4 bảng này đều cần lưu lại thời điểm tạo ra dòng dữ liệu (`created_at`) và thời điểm cập nhật cuối cùng (`updated_at`). 
Nếu ở Class Entity nào ta cũng lặp lại đoạn code định nghĩa 2 biến `createdAt`, `updatedAt` kèm các hàm `@PrePersist` thì mã nguồn sẽ dài dòng, vi phạm nguyên tắc **DRY (Don't Repeat Yourself)**.

### 💡 Giải pháp với `Abstract Class`
Chúng ta đã tạo ra một lớp trừu tượng có tên là `AbstractBaseEntity`:
```java
@MappedSuperclass
@Getter @Setter
public abstract class AbstractBaseEntity {
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() { ... }
}
```

Sau đó, cho các Class khác (ví dụ: `User`, `Transaction`) **kế thừa** (extends) từ lớp này:
```java
public class User extends AbstractBaseEntity implements UserDetails {
    // Chỉ cần khai báo username, password... Không cần viết lại createdAt nữa!
}
```

### 🧠 Giải thích bản chất OOP
- **Tại sao lại dùng `abstract class` mà không phải class thường?**
  Bởi vì `AbstractBaseEntity` chỉ là một cái "khuôn mẫu" (Template), nó không đại diện cho một thực thể có thật nào trong thế giới thực cả. Ta không bao giờ được phép khởi tạo nó bằng lệnh `new AbstractBaseEntity()`. Bằng cách đánh dấu `abstract`, ta báo cho trình biên dịch biết: *"Lớp này sinh ra chỉ để cho các lớp khác kế thừa, cấm khởi tạo trực tiếp"*.
- **Tính chất OOP thể hiện:** Kế thừa (Inheritance) và Trừu tượng (Abstraction).

---

## 2. Interface (Giao Diện) & Đa Hình (Polymorphism)

### 🚨 Vấn đề hệ thống cũ gặp phải
Trước đây, lớp `UserController` gọi trực tiếp đến Class `UserService`. Điều này tạo ra một sự **Kết dính chặt chẽ (Tight Coupling)**. 
Nếu ngày mai ta muốn viết một phiên bản `UserService` mới (ví dụ: `UserServiceV2` chạy nhanh hơn), ta sẽ phải vào `UserController` sửa lại toàn bộ code. Hơn nữa, việc gọi trực tiếp Class khiến cho việc viết Unit Test (tạo Mock Object) trở nên khó khăn.

### 💡 Giải pháp với `Interface`
Chúng ta đổi tên Class cũ thành `UserServiceImpl` (Lớp thực thi - Implementation).
Sau đó tạo ra một Interface mang tên `UserService` chỉ chứa các "hợp đồng" (hàm rỗng):

```java
// Chỉ định nghĩa hợp đồng, không có thân hàm (No body)
public interface UserService {
    UserResponseDTO createUser(UserRequestDTO dto);
    List<UserSummaryProjection> getAllUsers();
    // ...
}
```

Trong `UserServiceImpl`, ta khai báo `implements UserService`:
```java
@Service
public class UserServiceImpl implements UserService {
    // Cung cấp logic thực tế cho các hàm
    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) { ... }
}
```

Trong `UserController`, ta **chỉ phụ thuộc vào Interface**:
```java
@RestController
public class UserController {
    // Controller không cần biết nó đang dùng UserServiceImpl hay UserServiceV2. 
    // Nó chỉ cần biết biến này thỏa mãn hợp đồng "UserService".
    private final UserService userService;
}
```

### 🧠 Giải thích bản chất OOP
- **Tại sao lại chia Interface và Impl?** 
  Đây là việc áp dụng nguyên lý **Dependency Inversion** (chữ D trong bộ nguyên tắc thiết kế SOLID nổi tiếng). Các module cấp cao (Controller) không nên phụ thuộc vào module cấp thấp (Service Class), mà cả hai nên phụ thuộc vào điểm chung trừu tượng (Interface).
- **Tính chất OOP thể hiện:** Tính Đa Hình (Polymorphism). Nhờ có Interface, biến `UserService` trong Controller có thể trỏ tới vô số các phiên bản thực thi (Implementations) khác nhau (Ví dụ: `UserServiceImpl`, `MockUserServiceForTesting`, v.v.) mà không làm vỡ code của Controller.

> [!TIP]
> **Tóm lược để đi phỏng vấn:**
> - Hỏi: *"Em dùng Abstract Class để làm gì?"* -> Trả lời: *"Dạ em dùng để gom các trường chung (như createdAt) cho các Entity, giúp tái sử dụng code thông qua kế thừa. Em đánh dấu abstract để ngăn việc khởi tạo sai mục đích."*
> - Hỏi: *"Thế còn Interface?"* -> Trả lời: *"Dạ em dùng Interface cho các tầng Service để giảm sự phụ thuộc (Loose Coupling) giữa Controller và Service, tạo thuận lợi cho việc viết Unit Test và dễ dàng thay thế logic sau này (SOLID principle)."*

---

## 3. Phân biệt rõ Interface và Abstract Class

Đây là câu hỏi kinh điển bậc nhất trong các buổi phỏng vấn Java. Hãy ghi nhớ bảng so sánh "chí mạng" sau:

| Tiêu chí | Abstract Class (Lớp trừu tượng) | Interface (Giao diện) |
| :--- | :--- | :--- |
| **Bản chất** | Là một cái **khuôn mẫu** (đại diện cho mối quan hệ *IS-A* - "Là một"). Ví dụ: Con bò **là một** động vật. | Là một bản **hợp đồng** (đại diện cho mối quan hệ *CAN-DO* - "Có thể làm"). Ví dụ: Máy bay **có thể** bay. |
| **Tính Đa kế thừa**| Một Class **chỉ được kế thừa (extends) 1** Abstract Class duy nhất. | Một Class có thể **triển khai (implements) nhiều** Interface cùng lúc. |
| **Chứa code thực thi**| Có thể chứa các hàm đã viết sẵn code logic (như hàm `onCreate` trong `AbstractBaseEntity`). | Hầu hết là các hàm rỗng (chỉ có tên hàm, không có thân hàm). *(Từ Java 8 có thêm default method nhưng hạn chế dùng)*. |
| **Khai báo biến** | Có thể chứa đủ loại biến (private, protected, kiểu int, String). | Mọi biến khai báo đều tự động trở thành hằng số (`public static final`). |
| **Mục đích sử dụng**| Dùng để **chia sẻ code chung** (tránh lặp lại) cho các lớp có cùng bản chất. (Ví dụ: Các Entity đều cần `createdAt`). | Dùng để định nghĩa **hành vi chuẩn** cho các lớp không liên quan gì đến nhau, hoặc để giảm sự phụ thuộc (Loose Coupling) ở tầng Service. |

> [!TIP]
> **Mẹo trả lời thực tế từ dự án của ta:**
> "Dạ thưa anh/chị, Abstract Class em dùng khi các lớp con THỰC SỰ LÀ một loại của lớp cha và chia sẻ chung thuộc tính (các Entity kế thừa BaseEntity để dùng chung cột createdAt). Còn Interface em dùng ở tầng Service để quy định CÁC HÀNH VI mà Service đó bắt buộc phải có, giúp Controller không bị phụ thuộc cứng vào cách code thực thi bên trong, hỗ trợ dễ dàng viết Unit Test sau này."
