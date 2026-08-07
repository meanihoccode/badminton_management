package com.example.java_basic.controller;

import com.example.java_basic.dto.UserRequestDTO;
import com.example.java_basic.dto.UserResponseDTO;
import com.example.java_basic.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.security.Principal;
import com.example.java_basic.dto.TransactionResponseDTO;
import com.example.java_basic.dto.MatchHistoryResponseDTO;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Tạm mở CORS để test nhanh với React ở máy local
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // API lấy danh sách tất cả thành viên kèm số dư
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // API thêm người chơi mới
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO newUser = userService.createUser(dto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    // API Thanh toán nợ / Nạp quỹ
    // Ví dụ URL: POST /api/users/3/pay?amount=15000
    @PostMapping("/{id}/pay")
    public ResponseEntity<UserResponseDTO> payDebt(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0");
        }

        return ResponseEntity.ok(userService.payDebt(id, amount));
    } 

    @GetMapping("/me/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactions(Principal principal) {
        return ResponseEntity.ok(userService.getMyTransactions(principal.getName()));
    }

    @GetMapping("/me/matches")
    public ResponseEntity<List<MatchHistoryResponseDTO>> getMyMatches(Principal principal) {
        return ResponseEntity.ok(userService.getMyMatches(principal.getName()));
    }
}