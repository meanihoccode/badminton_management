package com.example.java_basic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SessionRequestDTO {
    @NotBlank(message = "Tên sân không được để trống")
    private String courtName;

    @NotNull(message = "Ngày đánh không được để trống")
    private LocalDate sessionDate;
}