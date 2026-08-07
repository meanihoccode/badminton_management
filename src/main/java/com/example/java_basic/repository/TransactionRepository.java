package com.example.java_basic.repository;

import com.example.java_basic.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Lấy toàn bộ lịch sử đóng/trừ tiền của một thành viên cụ thể
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}