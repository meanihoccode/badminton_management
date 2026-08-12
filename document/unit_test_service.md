# Tài Liệu Chuyên Sâu: Kiểm Thử Đơn Vị (Unit Test) Với JUnit 5 & Mockito

Tài liệu này giải thích chi tiết phương pháp và tư duy khi viết các bài kiểm thử tự động (Unit Test) cho Tầng Service (như `MatchServiceTest.java` và `BadmintonSessionServiceTest.java`) trong dự án.

## 1. Unit Test Là Gì Và Tại Sao Phải "Làm Giả"?

- **Unit Test (Kiểm thử đơn vị):** Là việc tách một hàm duy nhất ra (Ví dụ hàm tính tiền của `MatchService`) và nhét dữ liệu vào để xem nó chạy có ra kết quả đúng không.
- **Tại sao phải Mock (Làm giả):** Hàm `MatchService` thường gọi tới `UserRepository` để tìm dữ liệu trong MySQL. Nếu test mà phải chạy thẳng xuống MySQL thì quá trình test sẽ rất chậm, phụ thuộc vào mạng, và làm rác dữ liệu thật. Do đó, ta dùng công cụ **Mockito** để tạo ra các Repository "giả mạo" (Mocks). Chúng không kết nối với DB, mà chỉ trả về những kết quả được ta "nhét chữ vào miệng" sẵn.

## 2. Cấu Trúc Của Một File Test (Khung Xương)

Mỗi file test trong Spring Boot khi dùng Mockito thường tuân theo cấu trúc sau:

```java
@ExtendWith(MockitoExtension.class) // Bật tính năng Mockito cho file này
class MatchServiceTest {

    @Mock 
    // Tạo ra một nhân viên kho giả mạo
    private UserRepository userRepository;

    @InjectMocks 
    // Tạo ra class Service thật, nhưng lấy các nhân viên giả (Mocks) ở trên nhét vào nó
    private MatchService matchService;

    @BeforeEach
    // Hàm này tự động chạy TRƯỚC MỖI bài test để dọn dẹp và chuẩn bị dữ liệu (như khởi tạo User mẫu)
    void setUp() { ... }
}
```

## 3. Cấu Trúc 3 Chữ A (AAA) Trong Một Bài Test

Mỗi một hàm test (được gắn bùa `@Test`) luôn được viết theo tiêu chuẩn quốc tế **AAA**: **Arrange** (Chuẩn bị) - **Act** (Hành động) - **Assert** (Kiểm chứng).

### Bước 1: Arrange (Chuẩn Bị Hiện Trường)
Tại đây ta tạo ra các dữ liệu mẫu (Ví dụ: tạo 4 người chơi A1, A2, B1, B2) và "Dạy" cho Repository giả cách trả lời.
```java
// Dạy cho userRepository giả mạo:
// "Tí nữa thằng MatchService mà gọi hàm findById(1), thì mày ném thằng playerA1 này ra cho tao!"
when(userRepository.findById(1L)).thenReturn(Optional.of(playerA1));
```

### Bước 2: Act (Kéo Cò / Hành Động)
Gọi trực tiếp vào cái hàm mà chúng ta cần test của Service. Hàm này chạy chớp nhoáng (0.01s) vì mọi truy xuất DB đều đã bị chặn lại bởi các lệnh `when` ở Bước 1.
```java
// Gọi hàm ghi điểm trận đấu
matchService.recordMatchResult(dto);
```

### Bước 3: Assert (Kiểm Chứng Tội Phạm)
Mặc dù hàm đã chạy xong, ta không nhìn thấy gì trên màn hình cả. Ta phải dùng các lệnh Assert để nhờ máy tính đọ kết quả thực tế với đáp án xem có khớp không.
```java
// Khẳng định số tiền của playerA1 bây giờ bắt buộc phải là 5000đ. Nếu không đúng, báo đỏ chữ (Fail) bài test ngay lập tức.
assertEquals(new BigDecimal("5000"), playerA1.getBalance());
```

Ngoài ra, ta còn dùng lệnh `verify` để theo dõi xem hàm đó có "lười biếng" không.
```java
// Kiểm tra xem máy tính có chịu bấm nút "lưu vào cơ sở dữ liệu" (save) đúng 4 lần cho 4 người chơi không.
verify(userRepository, times(4)).save(any());
```

## 4. Test Case Bắt Lỗi Ngoại Lệ (Exception)

Không chỉ test trường hợp chạy đúng (Happy Path), một lập trình viên giỏi phải test cả trường hợp bắt lỗi. Ví dụ trong `BadmintonSessionServiceTest`, ta cố tình chốt sổ 2 lần:

```java
@Test
void testCloseSession_AlreadyCompleted_ThrowsException() {
    // 1. Arrange: Chỉnh trạng thái buổi đánh thành COMPLETED từ đầu
    mockSession.setStatus("COMPLETED");
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));

    // 2. Act & Assert gộp chung: Khẳng định rằng khi tao chạy hàm closeSession, mày BẮT BUỘC phải quăng ra cái lỗi IllegalStateException
    Exception exception = assertThrows(IllegalStateException.class, () -> {
        sessionService.closeSession(1L, BigDecimal.ZERO, BigDecimal.ZERO);
    });

    // Khẳng định câu chửi của hệ thống phải đúng chính xác từng chữ
    assertEquals("Buổi đánh này đã được chốt sổ rồi!", exception.getMessage());
}
```

---
**TỔNG KẾT:** Việc viết Service Test mất khá nhiều thời gian ban đầu để xây dựng các kịch bản Mock (nhét chữ vào miệng). Tuy nhiên, nó mang lại một tấm khiên vững chắc vô hình. Sau này khi bạn bàn giao dự án, nếu người mới vào lỡ tay xóa nhầm một dòng code tính tiền, họ chỉ cần gõ `gradlew test` là toàn bộ màn hình sẽ đỏ rực, ngăn chặn một thảm họa lỗi logic trước khi nó kịp tung ra ngoài thị trường.
