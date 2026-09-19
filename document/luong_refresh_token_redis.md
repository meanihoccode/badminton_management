# Tài liệu: Luồng hoạt động Refresh Token kết hợp Redis

Tài liệu này mô tả chi tiết cách thức hoạt động của cơ chế xác thực sử dụng kết hợp giữa **Access Token (JWT)**, **Refresh Token (UUID)** và **Redis** trong dự án, bao gồm cả nguyên lý hoạt động và cách code được triển khai.

---

## 1. Khái niệm cơ bản

- **Access Token (JWT):** Token dùng để truy cập vào các API được bảo vệ. Thời gian sống (TTL) của token này ngắn (ví dụ: 15 phút) để đảm bảo an toàn. Vì JWT là Stateless (không cần lưu server) nên API chạy rất nhanh.
- **Refresh Token (UUID):** Token dùng để xin cấp lại một Access Token mới khi Access Token cũ đã hết hạn. Thời gian sống của token này dài hơn nhiều (ví dụ: 7 ngày). Chúng ta sử dụng chuỗi ngẫu nhiên (UUID) thay vì JWT để bắt buộc hệ thống phải kiểm tra dưới database/cache.
- **Redis:** Database in-memory tốc độ siêu cao được dùng để lưu trữ danh sách các Refresh Token hợp lệ. Việc dùng Redis giúp chúng ta có thể dễ dàng xoá/thu hồi (revoke) Refresh Token bất cứ lúc nào (ví dụ: khi người dùng đăng xuất, hoặc khi nghi ngờ tài khoản bị hack).

---

## 2. Luồng hoạt động chi tiết & Minh hoạ bằng Code

### A. Luồng Đăng nhập (Login)
1. **Client** gửi thông tin đăng nhập (`username`, `password`) tới API `/api/auth/login`.
2. **Server** kiểm tra thông tin. Nếu hợp lệ:
   - Tạo **Access Token** (JWT, hết hạn sau 15 phút).
   - Tạo **Refresh Token** (UUID ngẫu nhiên).
   - Lưu **Refresh Token** vào **Redis** với Key là UUID, Value là `username` và thiết lập thời gian tự động huỷ (TTL) là 7 ngày.
3. Trả về cho Client cả `token` và `refreshToken`.

**Đoạn code lưu vào Redis lúc Đăng Nhập:** 
(Nằm tại `AuthServiceImpl.java` -> Hàm `login()`)
```java
        // 1. Tạo Refresh Token là một chuỗi ngẫu nhiên (UUID)
        String refreshToken = UUID.randomUUID().toString();
        
        // 2. Lưu vào Redis
        redisTemplate.opsForValue().set(
                refreshToken,              // Key: Là chuỗi UUID
                userDetails.getUsername(), // Value: Là username của người dùng
                refreshExpiration,         // Thời gian sống (ví dụ 7 ngày, quy ra Millisecond)
                TimeUnit.MILLISECONDS      // Đơn vị thời gian
        );
```

### B. Kiểm tra Access Token Hết hạn
Mỗi khi Client gọi các API cần bảo mật, Client phải đính kèm Access Token vào Header. Nếu Token quá 15 phút, thư viện JWT sẽ báo lỗi và Server sẽ chặn request.

**Đoạn code chặn Token hết hạn:**
(Nằm tại `JwtAuthenticationFilter.java` -> Dòng 42-63)
```java
        try {
            // Cố gắng giải mã token
            username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // ... set Authentication cho Spring Security ...
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("JWT Expired: " + e.getMessage());
            // CHÍNH LÀ ĐOẠN NÀY! 
            // Khi JWT hết hạn, nó sẽ văng ra lỗi ExpiredJwtException và bay thẳng vào khối catch này.
            // Chúng ta không set SecurityContext nên Spring Security mặc định coi như user chưa đăng nhập, trả về 401 Unauthorized.
        }
```

### C. Luồng Cấp lại Token (Refresh Token)
Khi nhận lỗi `401 Unauthorized` do Access Token hết hạn, Client ngầm gọi API `/api/auth/refresh` kèm theo **Refresh Token** cũ để xin cấp token mới.

1. **Kiểm tra Redis:** Server dùng `refreshToken` (Client gửi lên) làm Key để tìm `username` trong Redis.
   - Nếu *không thấy* (quá 7 ngày Redis tự xoá, hoặc bị admin xoá): Chặn lại, bắt đăng nhập.
   - Nếu *thấy*: Cấp Access Token mới.
2. **Xoay vòng Token (Token Rotation):** Để bảo mật tuyệt đối, mỗi Refresh Token chỉ dùng 1 lần. Server sẽ XOÁ Refresh Token cũ khỏi Redis, tạo một Refresh Token MỚI HOÀN TOÀN và lưu vào Redis.
3. Trả về cặp Token mới cho Client.

**Đoạn code cấp lại và xoay vòng Token:**
(Nằm tại `AuthServiceImpl.java` -> Hàm `refreshToken()`)
```java
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();
        
        // 1. TÌM TRONG REDIS
        String username = redisTemplate.opsForValue().get(requestRefreshToken);

        if (username == null) {
            throw new IllegalArgumentException("Refresh token is invalid or expired!");
        }

        // 2. TẠO ACCESS TOKEN MỚI
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails);
        
        // 3. XOÁ REFRESH TOKEN CŨ KHỎI REDIS
        redisTemplate.delete(requestRefreshToken);
        
        // 4. TẠO REFRESH TOKEN MỚI & LƯU REDIS (Token Rotation)
        String newRefreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                newRefreshToken, 
                userDetails.getUsername(), 
                refreshExpiration, 
                TimeUnit.MILLISECONDS
        );

        // Trả về kết quả...
    }
```

---

## 3. Hướng dẫn Test (Postman / REST Client)

**Yêu cầu:** Đảm bảo **Redis Server** đang chạy trên máy (port 6379).

### Bước 1: Test Login
- **Endpoint:** `POST http://localhost:8080/api/auth/login`
- **Body (JSON):**
  ```json
  {
      "username": "tentaikhoan",
      "password": "matkhau"
  }
  ```
- **Kết quả:** Trả về `token` (Access) và `refreshToken`. Hãy Copy chuỗi `refreshToken`.

### Bước 2: Test Refresh Token (Cấp mới token)
- **Endpoint:** `POST http://localhost:8080/api/auth/refresh`
- **Body (JSON):**
  ```json
  {
      "refreshToken": "chuỗi_refresh_token_vừa_copy_ở_bước_1"
  }
  ```
- **Kết quả:** Server trả về cặp token **mới**.

### Bước 3: Kiểm tra tính năng "Xoay vòng bảo mật" (Rotation)
- Bạn lấy đúng chuỗi `refreshToken` **cũ** ở Bước 1 và gọi API `/api/auth/refresh` **thêm một lần nữa**.
- **Kết quả mong đợi:** 
  Server sẽ báo lỗi `Refresh token is invalid or expired!`. Lý do là ở Bước 2, Server đã tự động xoá token cũ khỏi Redis để ngăn chặn kẻ gian tái sử dụng token.
