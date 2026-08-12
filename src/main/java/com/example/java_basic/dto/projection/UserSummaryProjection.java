package com.example.java_basic.dto.projection;

import java.math.BigDecimal;

public interface UserSummaryProjection {
    Long getId();
    String getFullName();
    String getEmail();
    String getRacketModel();
    BigDecimal getBalance();
}
