package com.example.java_basic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SessionRequestDTO {
    @NotBlank(message = "{val.court.notblank}")
    @Size(min = 2, max = 50, message = "{val.court.size}")
    private String courtName;

    @NotNull(message = "{val.date.notnull}")
    @PastOrPresent(message = "{val.date.past}")
    private LocalDate sessionDate;
}
