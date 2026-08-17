package com.example.java_basic.repository;

import com.example.java_basic.entity.BadmintonSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import com.example.java_basic.enums.SessionStatus;
import java.util.List;

@Repository
public interface BadmintonSessionRepository extends JpaRepository<BadmintonSession, Long> {
    // Tìm các buổi đánh theo trạng thái (VD: "OPEN" hoặc "COMPLETED")
    List<BadmintonSession> findByStatus(SessionStatus status);
}