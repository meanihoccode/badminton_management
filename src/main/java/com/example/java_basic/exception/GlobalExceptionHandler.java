package com.example.java_basic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final org.springframework.context.MessageSource messageSource;

    public GlobalExceptionHandler(org.springframework.context.MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Xử lý lỗi validation DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Xử lý lỗi đăng nhập sai mật khẩu (Spring Security ném ra)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        String message = messageSource.getMessage("err.auth.invalid_credentials", null, "Invalid username or password", org.springframework.context.i18n.LocaleContextHolder.getLocale());
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    // Xử lý lỗi không tìm thấy (Custom Exception)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), org.springframework.context.i18n.LocaleContextHolder.getLocale());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    // Xử lý lỗi logic (VD: chốt sổ buổi đánh đã hoàn thành)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), org.springframework.context.i18n.LocaleContextHolder.getLocale());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    // Xử lý lỗi dữ liệu không hợp lệ (VD: Tên đăng nhập đã tồn tại)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), org.springframework.context.i18n.LocaleContextHolder.getLocale());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
