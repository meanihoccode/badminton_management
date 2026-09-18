package com.example.java_basic.repository;

import com.example.java_basic.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND (:date IS NULL OR FUNCTION('DATE', t.createdAt) = :date) ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date, Pageable pageable);
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND (:date IS NULL OR FUNCTION('DATE', t.createdAt) = :date) AND (:type IS NULL OR (:type = 'PLUS' AND t.amount > 0) OR (:type = 'MINUS' AND t.amount < 0)) ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserIdAndDateAndType(@Param("userId") Long userId, @Param("date") LocalDate date, @Param("type") String type, Pageable pageable);

    boolean existsByExternalRef(String externalRef);
}