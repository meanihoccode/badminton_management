package com.example.java_basic.service;

import java.util.Map;

public interface AuthService {
    void register(Map<String, String> request);
    Map<String, String> login(Map<String, String> request);
}
