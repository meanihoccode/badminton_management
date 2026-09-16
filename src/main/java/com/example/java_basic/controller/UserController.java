package com.example.java_basic.controller;

import com.example.java_basic.constant.AppConstants;
import com.example.java_basic.dto.*;
import com.example.java_basic.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.security.Principal;

import com.example.java_basic.dto.projection.PlayerStatsProjection;
import com.example.java_basic.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RestController
@Validated
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    @GetMapping("/me")
    public ResponseEntity<com.example.java_basic.dto.UserResponseDTO> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.getMyProfile(principal.getName()));
    }

    // API lấy danh sách tất cả thành viên kèm số dư
    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication, @RequestParam(required = false) String keyword, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(AppConstants.ROLE_ADMIN));
        if (page != null && size != null) {
            return ResponseEntity.ok(userService.getAllUsersPaged(isAdmin, keyword, page, size));
        }
        return ResponseEntity.ok(userService.getAllUsers(isAdmin, keyword));
    }

    // API thêm người chơi mới
    @PostMapping
//    @RequestMapping(value = "/users", method = RequestMethod.POST) nếu k có request ở đầu class
//    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO newUser = userService.createUser(dto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDTO dto) {
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
            @RequestParam @Positive(message = "Tiền phải > 0") BigDecimal amount,
            @RequestParam(required = false) String note) {



        return ResponseEntity.ok(note != null ? userService.payDebt(id, amount, note) : userService.payDebt(id, amount));
    } 

    @GetMapping("/me/transactions")
    public ResponseEntity<?> getMyTransactions(Principal principal, @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date, @RequestParam(required = false) String type, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(userService.getMyTransactionsPaged(principal.getName(), date, type, page, size));
        }
        return ResponseEntity.ok(userService.getMyTransactions(principal.getName()));
    }

    @GetMapping("/me/matches")
    public ResponseEntity<?> getMyMatches(Principal principal, @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(userService.getMyMatchesPaged(principal.getName(), date, page, size));
        }
        return ResponseEntity.ok(userService.getMyMatches(principal.getName()));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<PlayerStatsProjection>> getLeaderboard() {
        return ResponseEntity.ok(userService.getTopActivePlayers());
    }
}