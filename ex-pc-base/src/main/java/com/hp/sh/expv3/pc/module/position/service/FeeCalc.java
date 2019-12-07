package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.constant.Precision;

public class FeeCalc {
    /**
     * 若 a1/b1 = a2/ b2
     * 求 b2
     * 用于 平仓时，等比例减少 初始保证金，
     *
     * @param a1
     * @param b1
     * @param a2
     * @return
     */
    public static BigDecimal slope(BigDecimal a1, BigDecimal a2, BigDecimal b1) {
        return a2.multiply(b1).divide(a1, Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
    }
    
}
