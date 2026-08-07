package com.example.java_basic.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class SessionResponseDTO {
    private Long id;
    private String courtName;
    private LocalDate sessionDate;
    private BigDecimal totalCourtFee;
    private BigDecimal shuttlecockFee;
    private String status; // OPEN hoặc COMPLETED
}
