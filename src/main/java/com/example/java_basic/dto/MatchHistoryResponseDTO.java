package com.example.java_basic.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MatchHistoryResponseDTO {
    private Long matchId;
    private String courtName;
    private LocalDate sessionDate;
    private String team; // A hoặc B
    private Integer teamAScore;
    private Integer teamBScore;
    private Integer pointDifference;
    private BigDecimal feeCalculated; // Tiền bị trừ (nếu thua)
}
