package com.example.java_basic.service.impl;

import com.example.java_basic.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    @Value("${google.recaptcha.secret-key}")
    private String recaptchaSecretKey;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Override
    public boolean verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("reCAPTCHA token is empty");
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
            requestMap.add("secret", recaptchaSecretKey);
            requestMap.add("response", token);

            ResponseEntity<Map> response = restTemplate.postForEntity(RECAPTCHA_VERIFY_URL, requestMap, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && Boolean.TRUE.equals(body.get("success"))) {
                return true;
            } else {
                log.warn("reCAPTCHA verification failed: {}", body);
                return false;
            }
        } catch (Exception e) {
            log.error("Error while verifying reCAPTCHA", e);
            return false;
        }
    }
}
