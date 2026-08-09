# Tài Liệu Chuyên Sâu: Bảo Mật Hệ Thống Bằng Spring Security & JWT

Tài liệu này đi sâu vào mã nguồn (source code) để giải thích cơ chế bảo mật và cách thức JWT hoạt động trong dự án Quản Lý Sân Cầu Lông.

## 1. Khởi Tạo Cấu Hình Bảo Mật (`SecurityConfig.java`)

Toàn bộ hệ thống phòng thủ của dự án nằm ở file `SecurityConfig.java`. Spring Security mặc định khóa chặn mọi thứ, nên chúng ta cần cấu hình để mở cửa cho những luồng dữ liệu hợp lệ.

### Điểm mấu chốt trong Code:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // Tắt CSRF vì chúng ta dùng JWT (không dùng Session Cookie)
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Cho phép Frontend (React chạy port 5173) gọi API
        .authorizeHttpRequests(auth -> auth
            // Mở cửa tự do cho 2 link đăng ký và đăng nhập
            .requestMatchers("/api/auth/**").permitAll() 
            // KHÓA CHẶT: Chỉ người dùng có ROLE_ADMIN mới được gọi 2 API sửa và xóa User
            .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
            // Mọi request còn lại (xem danh sách, tạo trận đấu...) bắt buộc phải có vé (Token hợp lệ)
            .anyRequest().authenticated()
        )
        // Chèn tấm khiên (Filter) kiểm tra vé vào trước quá trình xử lý của Spring
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

## 2. Trái Tim Của JWT (`JwtService.java`)

Khi người dùng nhập đúng tên đăng nhập và mật khẩu, hệ thống phải tạo ra cái "vé" JWT. Việc này được giao cho `JwtService`.

### 2.1. Cấu trúc của Token:
- Khóa bí mật (`SECRET_KEY`): Một chuỗi ngẫu nhiên rất dài. Máy chủ giữ kín khóa này. Nếu bị lộ, hacker có thể tự tạo ra vé giả.
- Thời gian sống (`EXPIRATION_TIME`): Cấu hình thường là 1 ngày (86,400,000 milliseconds). Hết thời gian này, vé tự hủy, người dùng phải đăng nhập lại.

### 2.2. Hàm Sinh Token (Generate Token):
```java
public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
            .subject(userDetails.getUsername()) // Lưu tên đăng nhập vào thân Token
            .issuedAt(new Date(System.currentTimeMillis())) // Thời điểm cấp vé
            .expiration(new Date(System.currentTimeMillis() + 86400000)) // 1 ngày sau hết hạn
            .signWith(getSigningKey()) // Ký tên bằng Khóa Bí Mật
            .compact();
}
```

## 3. Lính Gác Cửa (`JwtAuthenticationFilter.java`)

Đây là một `OncePerRequestFilter`, nghĩa là mỗi khi Frontend gửi Request lên, nó đều bị chặn lại 1 lần để xét giấy tờ.

### Luồng xử lý chi tiết trong hàm `doFilterInternal`:
1. **Tìm kiếm vé:**
   ```java
   final String authHeader = request.getHeader("Authorization");
   if (authHeader == null || !authHeader.startsWith("Bearer ")) {
       filterChain.doFilter(request, response); // Không có vé? Bỏ qua, để cho Spring xúm vào từ chối sau
       return;
   }
   jwt = authHeader.substring(7); // Cắt bỏ chữ "Bearer " để lấy đúng chuỗi JWT
   ```

2. **Dịch vé và xác minh:**
   - Hàm `jwtService.extractUsername(jwt)` sẽ giải mã bằng khóa bí mật. Nếu vé giả, nó văng lỗi ném ra ngoài ngay lập tức.
   - Nếu vé xịn, nó móc ra được chữ `username`.

3. **Cấp thẻ tạm thời (AuthenticationContext):**
   - Hệ thống đi hỏi Database xem `username` này có thật không, có Role gì (ví dụ: `ROLE_ADMIN`).
   - Dùng thông tin đó tạo ra một thẻ tên `UsernamePasswordAuthenticationToken` và đeo vào cổ request đó (thông qua `SecurityContextHolder`).
   - Nhờ cái "thẻ đeo" này, những hàm phía sau (được gắn `@PreAuthorize` hoặc config trong `SecurityConfig`) mới biết người này là Admin và cho phép họ xóa tài khoản.

## 4. Bảo Mật Phía Frontend (React)

Bảo mật ở Server là chưa đủ, Frontend cũng phải phối hợp:
1. Khi có Token trả về từ API `/api/auth/login`, React gọi hàm `localStorage.setItem('token', response.data.token)`.
2. Tạo một Interceptor (thường dùng Axios) để mọi lệnh Fetch API đều được tự động đính kèm Token:
   ```javascript
   axios.interceptors.request.use((config) => {
       const token = localStorage.getItem('token');
       if (token) {
           config.headers.Authorization = `Bearer ${token}`;
       }
       return config;
   });
   ```
3. Đọc dữ liệu Token: Nếu Payload của Token chứa chuỗi `ADMIN`, React sẽ rẽ nhánh UI, hiển thị ra các nút `[Xóa]`, `[Sửa]` để người dùng bấm. (Lưu ý: Kể cả Hacker có sửa React để nút này hiện ra, thì khi bấm lên Backend cũng bị Spring Security tát văng ra vì vé không có quyền).
