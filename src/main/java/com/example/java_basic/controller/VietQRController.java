package com.example.java_basic.controller;

import com.example.java_basic.service.impl.VietQRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class VietQRController {

    private final VietQRService vietQRService;

    @GetMapping("/generate-qr")
    public ResponseEntity<Map<String, String>> generateQR(
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String addInfo) {
        
        String finalAddInfo = addInfo != null && !addInfo.trim().isEmpty() ? addInfo : "Thanh toan tien san";
        String qrDataURL = vietQRService.generateQRCode(amount, finalAddInfo);
        
        Map<String, String> response = new HashMap<>();
        response.put("qrDataURL", qrDataURL);
        
        return ResponseEntity.ok(response);
    }
}