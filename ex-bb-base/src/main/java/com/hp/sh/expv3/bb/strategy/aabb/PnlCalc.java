/**
 * @author 10086
 * @date 2019/10/15
 */
package com.hp.sh.expv3.bb.strategy.aabb;

import java.math.BigDecimal;

import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;

public class PnlCalc {

	public static BigDecimal calcPnl(int longFlag, BigDecimal amt, BigDecimal entryPrice, BigDecimal closePrice) {
        return calcPnl(IntBool.isTrue(longFlag), amt, entryPrice, calcVolume(amt, closePrice, Precision.COMMON_PRECISION), Precision.COMMON_PRECISION);
    }

	public static BigDecimal calcVolume(BigDecimal amt, BigDecimal price, int scale) {
	    return amt.divide(price, scale, Precision.LESS).stripTrailingZeros();
	}

	private static BigDecimal calcPnl(boolean isLong, BigDecimal amt, BigDecimal entryPrice, BigDecimal closeVolume, int scale) {
        BigDecimal pnl = closeVolume.subtract(calcVolume(amt, entryPrice, scale));
        if (isLong) {
            return pnl.negate();
        } else {
            return pnl;
        }
    }

}
