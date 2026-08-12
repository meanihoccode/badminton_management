package com.example.java_basic.controller;

import com.example.java_basic.dto.MatchResultDTO;
import com.example.java_basic.exception.ResourceNotFoundException;
import com.example.java_basic.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // API nhập kết quả 1 set đấu
    @PostMapping("/record")
    public ResponseEntity<String> recordMatch(@Valid @RequestBody MatchResultDTO dto) {
        matchService.recordMatchResult(dto);
        return ResponseEntity.ok("Ghi nhận kết quả trận đấu và tính tiền thành công!");
    }
}