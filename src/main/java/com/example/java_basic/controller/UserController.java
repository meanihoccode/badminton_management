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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Tạm mở CORS để test nhanh với React ở máy local
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<com.example.java_basic.dto.UserResponseDTO> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.getMyProfile(principal.getName()));
    }

    private final UserService userService;
    private final MessageSource messageSource;

    // API lấy danh sách tất cả thành viên kèm số dư
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(userService.getAllUsers(isAdmin));
    }

    // API thêm người chơi mới
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO newUser = userService.createUser(dto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody com.example.java_basic.dto.UserUpdateRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal) {
        userService.deleteUser(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // API Thanh toán nợ / Nạp quỹ
    // Ví dụ URL: POST /api/users/3/pay?amount=15000
    @PostMapping("/{id}/pay")
    public ResponseEntity<UserResponseDTO> payDebt(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            String msg = messageSource.getMessage("error.invalid.amount", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(msg);
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

