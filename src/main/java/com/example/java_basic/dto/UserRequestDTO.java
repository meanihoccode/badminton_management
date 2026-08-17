package com.example.java_basic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.Email;
import com.example.java_basic.enums.Role;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Username không được để trống")
    private String username;

    // THÊM TRƯỜNG NÀY
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Tên người chơi không được để trống")
    private String fullName;

    private String racketModel;

    private Role role;
}

