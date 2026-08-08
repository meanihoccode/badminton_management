package com.example.java_basic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    @NotBlank(message = "Tên người chơi không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    private String email;

    private String racketModel;
}
