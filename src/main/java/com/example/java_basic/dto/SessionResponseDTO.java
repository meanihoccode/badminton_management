package com.example.java_basic.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.java_basic.enums.SessionStatus;

@Data
@Builder
public class SessionResponseDTO {
    private Long id;
    private String courtName;
    private LocalDate sessionDate;
    private BigDecimal totalCourtFee;
    private BigDecimal shuttlecockFee;
    private SessionStatus status; // OPEN hoặc COMPLETED
}
