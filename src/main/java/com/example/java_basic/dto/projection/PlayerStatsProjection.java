package com.example.java_basic.dto.projection;

/**
 * Interface Projection để nhận kết quả từ Native Query.
 */
public interface PlayerStatsProjection {
    String getUsername();
    Long getTotalMatches();
}
