# Tài Liệu: Ứng Dụng Thực Tế Các Chủ Đề Spring Basic

Tài liệu này giải thích chi tiết cách dự án Badminton Management đã tích hợp và sử dụng các khái niệm cơ bản (nhưng cực kỳ quan trọng) của Java Spring Framework.

---

## 1. Bean Scope (Phạm vi của Bean)

**Khái niệm dễ hiểu:** 
Hãy tưởng tượng Spring Container là một **quán phở**, và Bean là **cái bát**.
- **Singleton (Mặc định)**: Quán chỉ có đúng 1 cái bát. Khách A tới ăn, chủ quán múc phở vào bát đó. Đang ăn dở thì khách B tới, chủ quán lại đổ thêm phở của B vào chính cái bát đó. Kết quả là cả A và B ăn chung 1 bát, lộn xộn hết lên. Dùng Singleton chỉ an toàn khi "cái bát" đó không chứa dữ liệu thay đổi (không trạng thái - Stateless).
- **Prototype**: Khách nào tới quán, chủ quán cũng lấy 1 cái bát mới tinh từ trong tủ ra. Mỗi người 1 bát, không ai đụng chạm ai. Dùng Prototype cực kỳ an toàn khi bạn cần "cái bát" để chứa dữ liệu tạm thời cho từng Request (có trạng thái - Stateful).

**Áp dụng trong dự án:**
- **Lớp:** SessionCostCalculator (Người tính tiền)
- **Annotation:** @Scope("prototype")
- **Thực tế:** Khi Admin bấm nút "Chốt sổ" để chia tiền sân (BadmintonSessionServiceImpl.closeSession), chúng ta cần một "Người tính tiền" (SessionCostCalculator) để ghi chép lại tiền sân, tiền nước và số người tham gia vào cuốn sổ của họ. 
  - Nếu dùng Singleton (1 người tính tiền duy nhất), lỡ may có 2 Admin cùng chốt 2 buổi đánh khác nhau cùng lúc, ông tính tiền này sẽ bị nhầm lẫn, cộng tiền nước của Buổi 1 sang Buổi 2 gây sai lệch con số chia đều.
  - Bằng cách dùng @Scope("prototype"), mỗi lần gọi costCalculatorProvider.getObject(), Spring sẽ "tuyển" ngay 1 nhân viên tính tiền mới tinh chỉ để phục vụ cho buổi đánh đó. Tính xong thì cho nghỉ việc. Đảm bảo an toàn tuyệt đối về dữ liệu!

---

## 2. Bean Lifecycle (Vòng đời của Bean)

**Khái niệm:** Trong vòng đời của 1 Bean, Spring cho phép ta can thiệp vào 2 thời điểm nhạy cảm: ngay sau khi Bean được tiêm (Inject) xong toàn bộ Dependency, và ngay trước khi Bean bị phá hủy (ứng dụng tắt).

**Áp dụng trong dự án:**
- **Lớp:** `DatabaseSeeder`
- **Annotation:** `@PostConstruct` và `@PreDestroy`
- **Thực tế:** 
  - `@PostConstruct`: Khi khởi động Spring Boot, hàm `init()` sẽ tự động chạy. Nó thực hiện nhiệm vụ kiểm tra xem trong Database đã có tài khoản Admin nào chưa (rất hữu ích khi vừa setup database mới). Nếu chưa có, nó sẽ tự động `INSERT` một tài khoản Admin mặc định (`admin`/`123456`) để ta có tài khoản đăng nhập ngay lập tức mà không cần chọc vào SQL.
  - `@PreDestroy`: Khi tắt server (Ctrl+C), hàm `cleanup()` sẽ chạy để in ra Log và có thể dùng để đóng các tài nguyên (như ngắt kết nối Redis, xóa cache tạm thời,...).

---

## 3. RestTemplate (Gọi API bên ngoài)

**Khái niệm:** Đôi khi ứng dụng của chúng ta không chỉ thao tác với Database nội bộ mà còn cần lấy dữ liệu từ một hệ thống (Server) khác trên Internet (như API ngân hàng, gửi SMS, lấy thời tiết). `RestTemplate` là công cụ cốt lõi của Spring để thực hiện các HTTP Request (GET, POST,...) sang một máy chủ khác.

**Áp dụng trong dự án:**
- **Lớp:** `WeatherService` và `WeatherController`
- **Thực tế:** Chúng ta muốn có một tính năng nhỏ trên Dashboard của hệ thống Quản lý sân cầu lông để báo cho người chơi biết hôm nay trời có mưa không. 
- Dùng `new RestTemplate().getForObject(...)`, Server Spring Boot của chúng ta đóng vai trò là "Client" để gọi sang hệ thống Open-Meteo API, lấy về JSON chứa thông tin thời tiết (nhiệt độ, lượng mưa) tại tọa độ Hà Nội, sau đó trả về cho Frontend.

---

## 4. Native Query trong Spring Data JPA

**Khái niệm:** Spring Data JPA cung cấp các hàm rất tiện lợi như `findByEmail`, hoặc dùng JPQL (`@Query("SELECT u FROM User u...")`). Tuy nhiên, JPQL chỉ làm việc được ở mức Entity (Đối tượng). Khi phải đối mặt với các câu truy vấn Gom nhóm (GROUP BY), Tính toán (COUNT), Nối nhiều bảng phức tạp (JOIN) và Lấy giới hạn (LIMIT), ngôn ngữ SQL thuần (Native SQL) vẫn là mạnh mẽ và dễ viết nhất.

**Áp dụng trong dự án:**
- **Lớp:** `UserRepository` và `PlayerStatsProjection`
- **Annotation:** `@Query(value = "...", nativeQuery = true)`
- **Thực tế:** Ta xây dựng tính năng **Bảng Xếp Hạng (Leaderboard)** để vinh danh Top 5 người chơi chăm chỉ nhất (tham gia nhiều trận đấu nhất).
- Truy vấn này đòi hỏi phải `JOIN` bảng `users` với `match_participants`, dùng `GROUP BY` đếm số trận và `ORDER BY DESC LIMIT 5`. Rất khó để viết bằng JPQL. Dùng `nativeQuery = true` giải quyết bài toán trong 1 nốt nhạc.
- **Mẹo nâng cao (Projection):** Thay vì tạo 1 Entity cồng kềnh để hứng kết quả trả về, ta tạo 1 interface `PlayerStatsProjection` (chứa `getUsername()` và `getTotalMatches()`). Spring Data JPA sẽ tự động map (ánh xạ) kết quả của câu SQL thuần vào các hàm của Interface này cực kỳ gọn nhẹ!
