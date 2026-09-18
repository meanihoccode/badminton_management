package com.example.java_basic.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WebhookRequestDTO {
    private String transactionId;
    private BigDecimal amount;
    private String description;
    // Cac he thong thuc te (SePay/PayOS) co the co them cac truong:
    // private String gateway;
    // private String time;
}