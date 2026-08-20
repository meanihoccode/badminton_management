package com.example.java_basic.constant;

/**
 * Minh họa Final Class và String Pool.
 * 1. Final Class: Lớp này không thể bị kế thừa (extends). Điều này bảo vệ an toàn cho các hằng số.
 * 2. String Pool: Mọi biến String khai báo trực tiếp (literal) như "ROLE_ADMIN" đều được Java
 *    đưa vào String Pool. Khi dùng AppConstants.ROLE_ADMIN ở nhiều nơi, chúng cùng trỏ về 1 ô nhớ.
 */
public final class AppConstants {

    // Chặn khởi tạo object từ class này
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MEMBER = "ROLE_MEMBER";
    
    // Một phương thức final (dù class đã final thì phương thức mặc định không thể override, nhưng viết ra để minh họa)
    public static final String getDefaultWelcomeMessage() {
        return "Chào mừng đến với hệ thống Quản lý sân cầu lông!";
    }
}
