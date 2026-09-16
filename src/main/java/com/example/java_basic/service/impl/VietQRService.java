package com.example.java_basic.service.impl;

import com.example.java_basic.dto.VietQRRequestDTO;
import com.example.java_basic.dto.VietQRResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;

@Service
public class VietQRService {

    private final RestTemplate restTemplate;
    private static final String VIETQR_API_URL = "https://api.vietqr.io/v2/generate";

    @Value("${vietqr.bank.bin}")
    private Integer bankBin;

    @Value("${vietqr.bank.account-no}")
    private String accountNo;

    @Value("${vietqr.bank.account-name}")
    private String accountName;

    public VietQRService() {
        this.restTemplate = new RestTemplate();
    }

    public String generateQRCode(BigDecimal amount, String addInfo) {
        // 1. Chuẩn bị request body (Dữ liệu gửi đi)
        VietQRRequestDTO requestDTO = VietQRRequestDTO.builder()
                .accountNo(accountNo)
                .accountName(accountName)
                .acqId(bankBin)
                .amount(amount)
                .addInfo(addInfo)
                .format("text")
                .template("compact")
                .build();

        // 2. Chuẩn bị Header (Thông báo định dạng là JSON)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Đóng gói Body và Header vào HttpEntity
        HttpEntity<VietQRRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        try {
            // 4. Thực hiện lệnh POST
            ResponseEntity<VietQRResponseDTO> responseEntity = restTemplate.postForEntity(
                    VIETQR_API_URL, 
                    requestEntity, 
                    VietQRResponseDTO.class
            );

            // 5. Bóc tách dữ liệu trả về
            VietQRResponseDTO responseBody = responseEntity.getBody();
            if (responseBody != null && "00".equals(responseBody.getCode()) && responseBody.getData() != null) {
                return responseBody.getData().getQrDataURL(); // Chuỗi base64 của ảnh QR
            } else {
                throw new RuntimeException("VietQR API trả về lỗi: " + (responseBody != null ? responseBody.getDesc() : "Unknown error"));
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kết nối đến VietQR: " + e.getMessage());
        }
    }
}