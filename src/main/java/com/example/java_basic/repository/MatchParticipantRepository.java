package com.example.java_basic.repository;

import com.example.java_basic.entity.MatchParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    List<MatchParticipant> findByUserId(Long userId);
    Page<MatchParticipant> findByUserId(Long userId, Pageable pageable);
}