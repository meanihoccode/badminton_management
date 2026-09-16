package com.example.java_basic.repository;

import com.example.java_basic.entity.MatchParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    List<MatchParticipant> findByUserId(Long userId);
    Page<MatchParticipant> findByUserId(Long userId, Pageable pageable);
    @Query("SELECT mp FROM MatchParticipant mp WHERE mp.user.id = :userId AND (:date IS NULL OR mp.match.session.sessionDate = :date)")
    Page<MatchParticipant> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date, Pageable pageable);
}