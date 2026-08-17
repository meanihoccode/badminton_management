package com.example.java_basic.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "{val.username.notblank}")
    @Size(min = 3, max = 20, message = "{val.username.size}")
    private String username;
    @NotBlank(message = "{val.password.notblank}")
    @Size(min = 3, max = 20, message = "{val.password.size}")
    private String password;
}
