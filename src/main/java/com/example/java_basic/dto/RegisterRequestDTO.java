package com.example.java_basic.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String fullName;
    private String racketModel;
    private String role;
}
