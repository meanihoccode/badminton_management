package com.example.java_basic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;
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
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        String message = messageSource.getMessage("err.auth.invalid_credentials", null, "Invalid username or password", org.springframework.context.i18n.LocaleContextHolder.getLocale());
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // Xử lý lỗi không tìm thấy (Custom Exception)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), org.springframework.context.i18n.LocaleContextHolder.getLocale());
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Xử lý lỗi logic (VD: chốt sổ buổi đánh đã hoàn thành)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), org.springframework.context.i18n.LocaleContextHolder.getLocale());
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Xử lý lỗi dữ liệu không hợp lệ (VD: Tên đăng nhập đã tồn tại)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), org.springframework.context.i18n.LocaleContextHolder.getLocale());
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        // Lay thong bao loi dau tien
        String message = ex.getConstraintViolations().iterator().next().getMessage();
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
