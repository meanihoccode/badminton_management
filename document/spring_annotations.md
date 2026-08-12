# Từ Điển Các Annotation Thường Gặp Trong Dự Án Spring Boot

Trong Spring Boot, **Annotation** (ký tự `@` đứng trước một chữ cái) đóng vai trò như những chiếc "bùa chú". Chỉ cần gắn lên trên một Class hoặc một Hàm, Spring sẽ tự động đút đoạn code phức tạp ẩn giấu đằng sau vào chạy thay cho bạn.

Dưới đây là danh sách các "bùa chú" đã được sử dụng trong dự án Quản Lý Sân Cầu Lông:

## 1. Tầng Controller (Điều hướng API)
- `@RestController`: Đánh dấu class này là nơi tiếp nhận các yêu cầu (Request) từ Frontend (React/Postman). Chữ `Rest` ám chỉ mọi dữ liệu trả về sẽ tự động biến thành định dạng JSON.
- `@RequestMapping("/api/users")`: Gắn biển số nhà. Báo cho Spring biết mọi hàm trong class này đều bắt đầu bằng đường dẫn `/api/users`.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Ánh xạ các hành động của Frontend với từng hàm tương ứng (Xem, Thêm, Sửa, Xóa).
- `@RequestBody`: Ép Spring Boot lấy cục dữ liệu JSON mà Frontend gửi lên, rồi nhồi nó vào một cái Khuôn Java (DTO) tương ứng.
- `@PathVariable("id")`: Cắt lấy con số nằm trên đường dẫn URL để gán vào biến trong code (Ví dụ `/api/users/5` thì lấy số `5`).
- `@CrossOrigin`: Mở khóa "biên giới" cho API. Trình duyệt web mặc định có một lớp bảo vệ tên là CORS, nó cấm Frontend (chạy ở cổng `localhost:5173`) gọi sang Backend (chạy ở cổng `localhost:8080`) vì khác "địa chỉ nhà" (Origin). Việc gắn `@CrossOrigin` lên đầu Controller sẽ ra lệnh cho Spring Boot báo với trình duyệt rằng: "Không sao đâu, tôi cho phép Frontend ở cổng 5173 lấy dữ liệu của tôi". Nhờ đó React mới hiển thị được dữ liệu.

## 2. Tầng Service (Xử lý nghiệp vụ) & Core Spring Boot
- `@Component`: Khai báo chung chung rằng "Đây là một hạt đậu (Bean)". Spring Boot lúc khởi động sẽ đi rà soát tất cả các class gắn `@Component` (hoặc các biến thể của nó như `@Service`, `@Repository`, `@RestController`) để tạo ra các đối tượng (Object) duy nhất (Singleton) và lưu vào một cái rổ (IoC Container).
- `@Bean`: Thường được viết ở trên đầu một **Hàm** (Method) nằm trong class cấu hình (có `@Configuration`). Khác với `@Component` (gắn trên đầu Class), `@Bean` dùng để thủ công hướng dẫn Spring Boot cách tạo ra một đối tượng đặc biệt (Ví dụ: Cấu hình `SecurityFilterChain` hoặc `PasswordEncoder` trong file Security).
- `@Service`: Bản chất y hệt `@Component`, nhưng được đặt tên là `Service` để báo cho con người biết đây là lớp xử lý logic tính toán nghiệp vụ (chia tiền, cộng trừ điểm).
- `@Autowired`: Dùng để "Tiêm" (Inject) một Bean có sẵn từ giỏ đồ của Spring vào class hiện tại. Ví dụ, khi bạn cần dùng `UserRepository` trong `UserService`, bạn chỉ cần gắn `@Autowired` lên trên biến đó, Spring sẽ tự mang nó đến cho bạn mà không cần dùng hàm `new`.
- `@RequiredArgsConstructor` (Của thư viện Lombok): Chiếc bùa chú thần thánh thay thế hoàn toàn cho `@Autowired`. Nó tự động tạo ra hàm Constructor chứa các biến `final`, giúp tiêm phụ thuộc (Dependency Injection) một cách sạch sẽ, ngắn gọn và an toàn nhất (Cách mà dự án chúng ta đang dùng).
- `@Transactional`: Quản lý giao dịch (Transaction) của Database. Nếu trong hàm có 3 lệnh sửa DB, nhưng lệnh số 3 bị lỗi, Annotation này sẽ ra lệnh cho MySQL **Rollback** (Hoàn tác) lại toàn bộ, không cho 2 lệnh trước đó được lưu. Cực kỳ quan trọng để bảo vệ số tiền không bị trừ oan!

## 3. Tầng Repository & Entity (Giao tiếp Database)
- `@Entity`: Phù phép một Class bình thường thành một Cái Bảng trong cơ sở dữ liệu MySQL.
- `@Table(name = "users")`: Trỏ chính xác tên Bảng tương ứng dưới DB.
- `@Id` & `@GeneratedValue`: Chỉ định đâu là Khóa Chính (Primary Key) và cấu hình cho MySQL tự động tăng số thứ tự đó lên (Auto Increment).
- `@Column(nullable = false)`: Cấu hình cho cột trong DB không được phép để trống (NOT NULL).
- `@ManyToOne`, `@OneToMany`: Cấu hình mối quan hệ khóa ngoại (Ví dụ: 1 Trận đấu - Nhiều Người chơi tham gia).

## 4. Các Annotation Của Lombok (Viết Code Siêu Ngắn)
Thư viện Lombok giúp xóa sổ hoàn toàn sự rườm rà của Java Cũ.
- `@Data`: Tự động viết cho bạn hàng chục hàm Getter/Setter, `toString()`, `equals()`... Bạn không bao giờ phải thấy chúng chướng mắt trong code nữa.
- `@Builder`: Cho phép khởi tạo một Object cực kỳ ngầu và rõ ràng. (VD: `User.builder().fullName("A").email("B").build()`).
- `@NoArgsConstructor`: Tự viết hàm khởi tạo rỗng (Cần thiết cho Spring Data JPA).
- `@AllArgsConstructor`: Tự viết hàm khởi tạo nhận tất cả các tham số.

## 5. Các Annotation Đặc Biệt Khác
- `@PreAuthorize("hasRole('ADMIN')")` (Spring Security): Đặt chốt bảo vệ ngay trước cửa hàm. Chỉ ai cầm tấm thẻ ghi chữ ADMIN mới được phép bước vào hàm này.
- `@Scheduled(cron = "0 0 8 * * *")` (Spring Scheduler): Báo thức tự động. Đúng 8h sáng chạy đoạn code phía dưới.
- `@Mapper(componentModel = "spring")` (MapStruct): Ra lệnh cho thư viện MapStruct biến cái Interface rỗng tuếch thành một class copy/paste dữ liệu, và nhờ tham số `componentModel = "spring"`, MapStruct sẽ tự động gắn `@Component` ngầm vào class đó để giao cho Spring Boot quản lý.
- `@Mapping` (MapStruct): Dùng bên trong Interface của MapStruct để hướng dẫn cách copy dữ liệu khi tên biến của 2 bên không giống nhau. Ví dụ: `@Mapping(source = "match.session.courtName", target = "courtName")` nghĩa là lấy chữ `courtName` sâu tít bên trong `match.session` để gán vào `courtName` ở phía DTO.
- `@Mock`, `@InjectMocks` (Mockito): Dùng trong lúc viết Unit Test. Bùa chú này giúp "làm giả" (Fake) một kết nối Database để test hàm chạy siêu nhanh mà không cần rờ tới MySQL thật.

## 6. Các Annotation Của Java Core (Cốt lõi)
- `@Override`: Báo cho trình biên dịch (Compiler) biết rằng bạn đang **ghi đè** lại một hàm đã có sẵn ở class cha (hoặc Interface). Ví dụ trong `SecurityConfig`, hàm `securityFilterChain` không phải do ta tự nghĩ ra, mà là hàm của Spring, ta gắn `@Override` để ghi đè lại nội dung của nó theo ý mình. Nếu ta gõ sai tên hàm, Java sẽ báo lỗi ngay nhờ có bùa chú này.
