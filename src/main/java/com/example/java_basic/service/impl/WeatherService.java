package com.example.java_basic.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Minh họa sử dụng RestTemplate để gọi external API.
 */
@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    public String getCurrentWeather() {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=21.0285&longitude=105.8542&current_weather=true";
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "{\"error\": \"Không thể lấy dữ liệu thời tiết.\"}";
        }
    }
}