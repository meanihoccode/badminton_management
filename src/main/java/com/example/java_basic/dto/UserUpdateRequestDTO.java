package com.example.java_basic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    @NotBlank(message = "{val.fullname.notblank}")
    @Size(min = 2, max = 50, message = "{val.fullname.size}")
    private String fullName;

    @NotBlank(message = "{val.email.notblank}")
    @Email(message = "{val.email.format}")
    @Size(max = 100, message = "{val.email.size}")
    private String email;

    @Size(max = 100, message = "{val.racket.size}")
    private String racketModel;
}
