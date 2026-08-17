package com.example.java_basic.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 20, message = "Username phải từ 3 đến 20 ký tự")
    private String username;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 3, max = 20, message = "Mật khẩu phải từ 3 đến 20 ký tự")
    private String password;
}
