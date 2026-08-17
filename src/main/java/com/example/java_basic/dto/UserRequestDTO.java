package com.example.java_basic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.example.java_basic.enums.Role;

@Data
public class UserRequestDTO {
    @NotBlank(message = "{val.username.notblank}")
    @Size(min = 3, max = 20, message = "{val.username.size}")
    private String username;

    // THÊM TRƯỜNG NÀY
    @NotBlank(message = "{val.password.notblank}")
    @Size(min = 6, max = 20, message = "{val.password.size}")
    private String password;

    @NotBlank(message = "{val.email.notblank}")
    @Email(message = "{val.email.format}")
    @Size(max = 100, message = "{val.email.size}")
    private String email;

    @NotBlank(message = "{val.fullname.notblank}")
    @Size(min = 2, max = 50, message = "{val.fullname.size}")
    private String fullName;

    @Size(max = 100, message = "{val.racket.size}")
    private String racketModel;

    private Role role;
}
