package com.example.java_basic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VietQRResponseDTO {
    private String code;
    private String desc;
    private VietQRData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VietQRData {
        private String qrCode;
        private String qrDataURL;
    }
}