package com.example.java_basic.repository;

import com.example.java_basic.entity.MatchParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    // Lấy lịch sử tham gia các trận đấu của một người chơi
    List<MatchParticipant> findByUserId(Long userId);

    // Lấy danh sách những người tham gia trong 1 trận đấu cụ thể
    List<MatchParticipant> findByMatchId(Long matchId);
}