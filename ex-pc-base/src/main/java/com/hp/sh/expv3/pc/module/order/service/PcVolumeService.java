/**
 * @author 10086
 * @date 2019/10/15
 */
package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;

import com.hp.sh.expv3.utils.IntBool;

public class PcVolumeService {

    public static BigDecimal calcVolume(BigDecimal amt, BigDecimal price) {
        return amt.divide(price, Precision.DIVIDE_SCALE, Precision.LESS).stripTrailingZeros();
    }

    public static BigDecimal calcPnl(int longFlag, BigDecimal amt, BigDecimal entryPrice, BigDecimal closePrice) {
        return calcPnl(amt, entryPrice, calcVolume(amt, closePrice), IntBool.isTrue(longFlag));
    }

    public static BigDecimal calcPnl(BigDecimal amt, BigDecimal entryPrice, BigDecimal closeVolume, boolean isLong) {
        BigDecimal pnl = closeVolume.subtract(calcVolume(amt, entryPrice));
        if (isLong) {
            return pnl.negate();
        } else {
            return pnl;
        }
    }

}