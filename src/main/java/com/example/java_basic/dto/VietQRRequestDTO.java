package com.example.java_basic.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class VietQRRequestDTO {
    private String accountNo;
    private String accountName;
    private Integer acqId;
    private BigDecimal amount;
    private String addInfo;
    private String format;
    private String template;
}