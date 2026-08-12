package com.example.java_basic.service;

import com.example.java_basic.dto.SessionRequestDTO;
import com.example.java_basic.dto.SessionResponseDTO;
import java.math.BigDecimal;

public interface BadmintonSessionService {
    SessionResponseDTO createSession(SessionRequestDTO dto);
    void closeSession(Long sessionId, BigDecimal totalCourtFee, BigDecimal shuttlecockFee);
}
