# Hướng dẫn tạo Custom Validation (Annotation) trong Spring Boot

> [!NOTE]
> **Custom Validation** là kỹ thuật tự tạo ra các Annotation riêng (như `@UniqueUsername`, `@UniqueEmail`) để kiểm tra tính hợp lệ của dữ liệu đầu vào. Thay vì viết những khối lệnh `if/else` dài dòng trong tầng `Service`, chúng ta chuyển toàn bộ trọng trách kiểm tra này về tầng `DTO`.

> [!TIP]
> Kỹ thuật này giúp code trở nên gọn gàng, chuyên nghiệp và tuân thủ nguyên tắc **Decoupling** (Tách biệt rõ ràng giữa logic nghiệp vụ và logic kiểm tra dữ liệu).

---

## 1. Thành phần của một Custom Validator

Để tạo một Custom Validation, bạn luôn cần **2 file**:
1. **File Interface (Annotation):** Nơi định nghĩa tên của Annotation (cái nhãn dán).
2. **File Class (Validator):** Nơi chứa logic xử lý thực tế (bộ não).

---

## 2. Bước 1: Tạo Annotation (Nhãn dán)

Ví dụ tạo Annotation `@UniqueUsername`:

```java
package com.example.java_basic.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

// Cho phép dán Annotation này lên các thuộc tính (FIELD)
@Target({ElementType.FIELD})
// Giữ Annotation này tồn tại trong lúc phần mềm đang chạy (RUNTIME)
@Retention(RetentionPolicy.RUNTIME)
// Chỉ định Class sẽ chịu trách nhiệm xử lý logic cho Annotation này
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
    
    // Câu thông báo lỗi mặc định nếu dữ liệu không hợp lệ. 
    // Có thể truyền key trong file messages.properties (VD: {err.auth.username_exists})
    String message() default "Tên đăng nhập đã tồn tại";

    // Hai hàm bắt buộc phải có theo chuẩn của JSR 380 (Bean Validation)
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

---

## 3. Bước 2: Tạo Validator (Bộ não xử lý)

Tạo class `UniqueUsernameValidator` đã được chỉ định ở trên. Class này phải `implements ConstraintValidator`.

```java
package com.example.java_basic.validator;

import com.example.java_basic.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

// Generic: <Tên_Annotation, Kiểu_Dữ_Liệu_Của_Biến_Được_Gắn>
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    // Điểm ưu việt: Spring cho phép @Autowired các Bean (Repository/Service) ngay trong Validator
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        // Bỏ qua nếu giá trị null hoặc rỗng. Việc kiểm tra rỗng nên nhường cho @NotBlank
        if (username == null || username.trim().isEmpty()) {
            return true; 
        }
        
        // Truy vấn CSDL: Nếu danh sách rỗng (chưa ai dùng) -> return true (Hợp lệ)
        // Ngược lại nếu tìm thấy người dùng -> return false (Báo lỗi)
        return userRepository.findByUsername(username).isEmpty();
    }
}
```

> [!IMPORTANT]
> Mặc dù `UniqueUsernameValidator` không hề có `@Component` hay `@Service`, Spring Boot vẫn tự động nhận diện nó là một Spring Bean nhờ vào interface `ConstraintValidator`, do đó bạn có thể dùng `@Autowired` thoải mái.

---

## 4. Bước 3: Gắn lên DTO để sử dụng

Sau khi tạo xong, bạn chỉ việc đem cái "nhãn dán" đó gắn lên trường dữ liệu tương ứng trong các DTO:

```java
package com.example.java_basic.dto;

import com.example.java_basic.validator.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "{val.username.notblank}")
    @UniqueUsername // <--- Gắn custom annotation vào đây!
    private String username;

    private String password;
}
```

> [!WARNING]
> **Đừng quên:** Ở Controller, bạn bắt buộc phải có `@Valid` hoặc `@Validated` đặt trước DTO thì Spring mới kích hoạt việc quét và chạy các Annotation Validation.

```java
@PostMapping
public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO dto) {
    // Nếu username bị trùng, Spring sẽ chặn request lại và ném ra 400 Bad Request ngay tại đây.
    // Code bên trong Controller/Service không cần phải lo việc kiểm tra trùng lặp nữa!
    userService.createUser(dto);
    return ResponseEntity.ok().build();
}
```