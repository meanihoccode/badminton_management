package com.example.java_basic.controller;

import com.example.java_basic.entity.User;
import com.example.java_basic.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.payos.model.webhooks.Webhook;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final com.example.java_basic.repository.UserRepository userRepository;

    // API de Frontend goi khi user bam nut Nap Tien
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPaymentLink(
            @RequestBody Map<String, Object> body, 
            Authentication authentication) {
        
        System.out.println(">>> Nhan request tao payment link: " + body);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();
        
        int amount = Integer.parseInt(body.get("amount").toString());
        String returnUrl = body.getOrDefault("returnUrl", "http://localhost:5173/").toString();
        
        String checkoutUrl = paymentService.createPaymentLink(userId, amount, returnUrl);
        return ResponseEntity.ok(Map.of("checkoutUrl", checkoutUrl));
    }

    // API de PayOS goi khi co bien dong so du
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody Webhook request) {
        paymentService.processWebhook(request);
        return ResponseEntity.ok(Map.of("message", "success"));
    }
}