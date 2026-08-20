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
import com.example.java_basic.dto.projection.PlayerStatsProjection;
import com.example.java_basic.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @GetMapping("/me")
    public ResponseEntity<com.example.java_basic.dto.UserResponseDTO> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.getMyProfile(principal.getName()));
    }

    // API lấy danh sách tất cả thành viên kèm số dư
    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(com.example.java_basic.constant.AppConstants.ROLE_ADMIN));
        if (page != null && size != null) {
            return ResponseEntity.ok(userService.getAllUsersPaged(isAdmin, page, size));
        }
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
    @PostMapping("/{id}/pay")
    public ResponseEntity<UserResponseDTO> payDebt(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String note) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            String msg = messageSource.getMessage("error.invalid.amount", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(msg);
        }

        return ResponseEntity.ok(note != null ? userService.payDebt(id, amount, note) : userService.payDebt(id, amount));
    } 

    @GetMapping("/me/transactions")
    public ResponseEntity<?> getMyTransactions(Principal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(userService.getMyTransactionsPaged(principal.getName(), page, size));
        }
        return ResponseEntity.ok(userService.getMyTransactions(principal.getName()));
    }

    @GetMapping("/me/matches")
    public ResponseEntity<?> getMyMatches(Principal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(userService.getMyMatchesPaged(principal.getName(), page, size));
        }
        return ResponseEntity.ok(userService.getMyMatches(principal.getName()));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<PlayerStatsProjection>> getLeaderboard() {
        return ResponseEntity.ok(userRepository.getTopActivePlayers());
    }
}