package com.example.java_basic.controller;

import com.example.java_basic.dto.SessionRequestDTO;
import com.example.java_basic.dto.SessionResponseDTO;
import com.example.java_basic.service.BadmintonSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BadmintonSessionController {

    private final BadmintonSessionService sessionService;

    // API tạo buổi đánh mới
    @PostMapping
    public ResponseEntity<SessionResponseDTO> createSession(@Valid @RequestBody SessionRequestDTO dto) {
        return new ResponseEntity<>(sessionService.createSession(dto), HttpStatus.CREATED);
    }

    // API chốt sổ (chia tiền sân, tiền cầu)
    // Ví dụ URL: POST /api/sessions/1/close?courtFee=400000&shuttlecockFee=150000
    @PostMapping("/{sessionId}/close")
    public ResponseEntity<String> closeSession(
            @PathVariable Long sessionId,
            @RequestParam BigDecimal courtFee,
            @RequestParam BigDecimal shuttlecockFee) {

        sessionService.closeSession(sessionId, courtFee, shuttlecockFee);
        return ResponseEntity.ok("Chốt sổ buổi đánh thành công!");
    }
}