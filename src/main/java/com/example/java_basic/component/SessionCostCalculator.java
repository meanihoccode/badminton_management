package com.example.java_basic.component;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Minh họa Bean Scope: Prototype.
 * Bean này có chứa trạng thái (stateful: totalCost, playerCount).
 * Nếu để Singleton, nhiều Admin chốt sổ cùng lúc sẽ bị ghi đè dữ liệu.
 * Dùng Prototype đảm bảo mỗi lần xin Bean, Spring sẽ cấp 1 object mới tinh.
 */
@Component
@Scope("prototype")
public class SessionCostCalculator {

    private BigDecimal totalCost = BigDecimal.ZERO;
    private int playerCount = 0;

    public void addCourtFee(BigDecimal fee) {
        this.totalCost = this.totalCost.add(fee);
    }

    public void addWaterFee(BigDecimal fee) {
        this.totalCost = this.totalCost.add(fee);
    }

    public void setPlayerCount(int count) {
        this.playerCount = count;
    }

    public BigDecimal calculateCostPerPlayer() {
        if (playerCount == 0) return BigDecimal.ZERO;
        return totalCost.divide(BigDecimal.valueOf(playerCount), 2, RoundingMode.HALF_UP);
    }
}
