package com.example.java_basic.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String racketModel;
    private BigDecimal balance;
    private String latestReceipt; // Để ReactJS tô màu đỏ nếu âm tiền, màu xanh nếu dương
}
