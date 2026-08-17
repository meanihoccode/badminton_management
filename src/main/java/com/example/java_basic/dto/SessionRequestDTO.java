package com.example.java_basic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SessionRequestDTO {
    @NotBlank(message = "Tên sân không được để trống")
    @Size(min = 2, max = 50, message = "Tên sân phải từ 2 đến 50 ký tự")
    private String courtName;

    @NotNull(message = "Ngày đánh không được để trống")
    @PastOrPresent(message = "Ngày đánh không được ở tương lai")
    private LocalDate sessionDate;
}
