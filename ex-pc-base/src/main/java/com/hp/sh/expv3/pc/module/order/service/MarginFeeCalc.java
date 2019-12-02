package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 保证金计算器
 * @author lw
 *
 */
public class MarginFeeCalc {
	
    public static BigDecimal calcVolume(BigDecimal amt, BigDecimal price) {
        return amt.divide(price, Precision.DIVIDE_SCALE, Precision.LESS).stripTrailingZeros();
    }

	public static BigDecimal calcMargin(boolean isClose, BigDecimal orderMarginRatio, BigDecimal volume) {
        return isClose ? BigDecimal.ZERO : orderMarginRatio.multiply(volume).stripTrailingZeros();
    }

	public static BigDecimal calcOpenFee(BigDecimal feeRatio, BigDecimal volume) {
		return calcFee(feeRatio, volume);
	}

	public static BigDecimal calcCloseFee(BigDecimal feeRatio, BigDecimal volume) {
		return calcFee(feeRatio, volume);
	}

	private static BigDecimal calcFee(BigDecimal feeRatio, BigDecimal volume) {
		return Optional.ofNullable(feeRatio).orElse(BigDecimal.ZERO).multiply(volume).stripTrailingZeros();
	}

	public static BigDecimal getFilledAmt() {
		// TODO Auto-generated method stub
		return null;
	}

	public static BigDecimal getFilledVolume() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
