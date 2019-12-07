/**
 * @author 10086
 * @date 2019/10/15
 */
package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.utils.IntBool;

@Service
public class PcBaseValueService {

	/**
	 * 计算成交量
	 * @param amt
	 * @param price
	 * @param scale
	 * @return
	 */
    public BigDecimal calcVolume(BigDecimal amt, BigDecimal price, int scale) {
        return amt.divide(price, scale, Precision.LESS).stripTrailingZeros();
    }

    public BigDecimal calcPnl(int longFlag, BigDecimal amt, BigDecimal entryPrice, BigDecimal closePrice, int scale) {
        return calcPnl(IntBool.isTrue(longFlag), amt, entryPrice, calcVolume(amt, closePrice, scale), scale);
    }

    public BigDecimal calcPnl(boolean isLong, BigDecimal amt, BigDecimal entryPrice, BigDecimal closeVolume, int scale) {
        BigDecimal pnl = closeVolume.subtract(calcVolume(amt, entryPrice, scale));
        if (isLong) {
            return pnl.negate();
        } else {
            return pnl;
        }
    }

}