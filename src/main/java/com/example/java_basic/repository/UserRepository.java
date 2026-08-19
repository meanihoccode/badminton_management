package com.example.java_basic.repository;

import com.example.java_basic.entity.User;
import com.example.java_basic.dto.projection.PlayerStatsProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.java_basic.dto.projection.UserSummaryProjection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA tự động dịch tên hàm thành câu SQL: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Dùng HQL để lấy danh sách những người đang nợ tiền quỹ (balance < 0)
    @Query("SELECT u FROM User u WHERE u.balance < 0")
    List<User> findUsersInDebt();

    // Dùng Projection để chỉ SELECT đúng các cột cần thiết (id, username, fullName, balance) thay vì SELECT *
    List<UserSummaryProjection> findAllProjectedBy();

    // Native Query: Lấy Top 5 người chơi tham gia nhiều trận đấu nhất
    @Query(value = "SELECT u.username, COUNT(mp.match_id) AS totalMatches FROM users u JOIN match_participants mp ON u.id = mp.user_id GROUP BY u.id ORDER BY totalMatches DESC LIMIT 5", nativeQuery = true)
    List<PlayerStatsProjection> getTopActivePlayers();
}
