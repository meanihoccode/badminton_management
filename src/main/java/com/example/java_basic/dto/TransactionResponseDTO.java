package com.example.java_basic.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String transactionType;
    private String description;
    private LocalDateTime createdAt;
}
