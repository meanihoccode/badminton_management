package com.example.java_basic.repository;

import com.example.java_basic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA tự động dịch tên hàm thành câu SQL: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // Dùng HQL để lấy danh sách những người đang nợ tiền quỹ (balance < 0)
    @Query("SELECT u FROM User u WHERE u.balance < 0")
    List<User> findUsersInDebt();
}