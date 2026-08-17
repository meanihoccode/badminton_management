package com.example.java_basic.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatchResultDTO {
    @NotNull(message = "{val.session.notnull}")
    private Long sessionId;

    @Min(value = 0, message = "{val.score.min}")
    private int teamAScore;

    @Min(value = 0, message = "{val.score.min}")
    private int teamBScore;

    @NotNull(message = "{val.player.notnull}") private Long playerA1Id;
    @NotNull(message = "{val.player.notnull}") private Long playerA2Id;
    @NotNull(message = "{val.player.notnull}") private Long playerB1Id;
    @NotNull(message = "{val.player.notnull}") private Long playerB2Id;
}
