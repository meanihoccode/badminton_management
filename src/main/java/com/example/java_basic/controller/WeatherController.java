package com.example.java_basic.controller;

import com.example.java_basic.service.impl.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getWeather() {
        return weatherService.getCurrentWeather();
    }
}
