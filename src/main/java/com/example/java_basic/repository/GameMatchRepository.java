package com.example.java_basic.repository;

import com.example.java_basic.entity.GameMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameMatchRepository extends JpaRepository<GameMatch, Long> {
    // Lấy toàn bộ các set đấu thuộc về một buổi đánh cụ thể
    List<GameMatch> findBySessionId(Long sessionId);
}