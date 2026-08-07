package com.example.java_basic.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatchResultDTO {
    @NotNull(message = "ID buổi đánh là bắt buộc")
    private Long sessionId;

    @Min(value = 0, message = "Điểm số không được âm")
    private int teamAScore;

    @Min(value = 0, message = "Điểm số không được âm")
    private int teamBScore;

    @NotNull private Long playerA1Id;
    @NotNull private Long playerA2Id;
    @NotNull private Long playerB1Id;
    @NotNull private Long playerB2Id;
}