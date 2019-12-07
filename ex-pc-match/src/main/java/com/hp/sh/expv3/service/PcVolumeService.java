/**
 * @author 10086
 * @date 2019/10/15
 */
package com.hp.sh.expv3.service;

import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.util.DecimalUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PcVolumeService {

    public BigDecimal calcVolume(BigDecimal amt, BigDecimal price, int scale) {
        return amt.divide(price, scale, DecimalUtil.LESS).stripTrailingZeros();
    }

    public BigDecimal calcPnl(int longFlag, BigDecimal amt, BigDecimal entryPrice, BigDecimal closePrice, int scale) {
        return calcPnl(CommonConst.isYes(longFlag), amt, entryPrice, calcVolume(amt, closePrice, scale), scale);
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