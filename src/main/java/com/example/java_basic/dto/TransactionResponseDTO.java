package com.example.java_basic.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.java_basic.enums.TransactionType;

@Data
@Builder
public class TransactionResponseDTO {
    private Long id;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private LocalDateTime createdAt;
}
