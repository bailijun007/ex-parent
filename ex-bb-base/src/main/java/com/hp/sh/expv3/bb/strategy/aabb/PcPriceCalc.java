package com.hp.sh.expv3.bb.strategy.aabb;

import com.hp.sh.expv3.utils.math.DecimalUtil;
import com.hp.sh.expv3.utils.math.Precision;

import java.math.BigDecimal;

/**
 * AABB价格计算器
 * asset : A
 * symbol : A_B
 * contract : nB
 * <p>
 * example : BTC -> BTC_USD , 每张合约面值 1 USD，代表 价值1USD 的BTC
 * <p>
 * case : AABB
 */
public class PcPriceCalc {

    /**
     * 求强平价：即在 强平价时 平仓，收回的保证金占整个仓位价值 的比率 = 维持仓位保证金（纯粹的仓位维持保证金率，不包括其他的手续费率）
     *
     * @param isLong
     * @param openPrice
     * @param amt
     * @param posMargin
     * @param posHoldMarginRatio
     * @return
     */
    public static BigDecimal calcLiqPrice(BigDecimal posHoldMarginRatio, boolean isLong, BigDecimal openPrice, BigDecimal amt, BigDecimal posMargin, int scale) {
        BigDecimal holdVolume = PnlCalc.calcVolume(amt, openPrice, scale);
        /**
         * op: open price
         * cp: close price
         *
         * ( posMargin + pnl ) / ( amt / cp ) = posHoldMarginRatio
         */
        if (isLong) {
            /**
             * ( posMargin + pnl ) / ( amt / cp ) = posHoldMarginRatio
             * ( posMargin + (amt /op ) - (amt/cp) ) / ( amt / cp ) = posHoldMarginRatio
             * ( posMargin + (amt /op ) ) / ( amt / cp ) -1 = posHoldMarginRatio
             * ( posMargin + (amt /op ) ) / ( amt / cp ) = posHoldMarginRatio + 1
             * ( posMargin + (amt /op ) ) /  (posHoldMarginRatio + 1) =  ( amt / cp )
             * cp =  amt * (posHoldMarginRatio + 1) / ( posMargin + (amt /op ) )
             * cp =  amt * (posHoldMarginRatio + 1) / ( posMargin + v )
             */
            return amt.multiply(posHoldMarginRatio.add(BigDecimal.ONE)).divide(posMargin.add(holdVolume), Precision.COMMON_PRECISION, DecimalUtil.MORE).max(BigDecimal.ZERO).stripTrailingZeros();
        } else {
            /**
             * ( posMargin + pnl ) / ( amt / cp ) = posHoldMarginRatio
             * ( posMargin + (amt /cp ) - (amt/op) ) / ( amt / cp ) = posHoldMarginRatio
             * [ posMargin - (amt/op) + (amt /cp) ] / ( amt / cp ) = posHoldMarginRatio
             * [( posMargin - (amt/op) ) / ( amt / cp ) ] + 1 = posHoldMarginRatio
             * [( posMargin - (amt/op) ) / ( amt / cp ) ] = posHoldMarginRatio - 1
             * [( posMargin - (amt/op) ) / (posHoldMarginRatio - 1) ] = ( amt / cp )
             *  ( posMargin - (amt/op) ) / (posHoldMarginRatio - 1)   = ( amt / cp )
             *  cp / (posHoldMarginRatio - 1) = amt / ( posMargin - (amt/op) )
             * cp = amt * (posHoldMarginRatio - 1) / (posMargin - v)
             */
            BigDecimal lp = amt.multiply(posHoldMarginRatio.subtract(BigDecimal.ONE)).divide(posMargin.subtract(holdVolume), Precision.COMMON_PRECISION, DecimalUtil.MORE);
            if (lp.compareTo(BigDecimal.ZERO) < 0) {
                lp = BigDecimal.valueOf(100000000L);
            }
            return lp.stripTrailingZeros();
        }
    }

    /**
     * 计算破产价，即所有保证金全部亏损
     *
     * @param isLong
     * @param openPrice
     * @param amt
     * @param margin    实际保证金
     * @return
     */
    public static BigDecimal calcBankruptPrice(boolean isLong, BigDecimal openPrice, BigDecimal amt, BigDecimal margin, int scale) {
        BigDecimal volume = PnlCalc.calcVolume(amt, openPrice, scale);
        if (isLong) {
            return amt.divide(volume.add(margin), scale, DecimalUtil.MORE).max(BigDecimal.ZERO).stripTrailingZeros();
        } else {
            BigDecimal bp = amt.divide(volume.subtract(margin), scale, DecimalUtil.LESS);
            if (bp.compareTo(BigDecimal.ZERO) < 0) {
                return BigDecimal.valueOf(100000000L);
            }
            return bp.stripTrailingZeros();
        }
    }

    /**
     * 计算持仓均价
     *
     * @param isLong
     * @param baseValue
     * @param amt
     * @return
     */
    public static BigDecimal calcEntryPrice(boolean isLong, BigDecimal baseValue, BigDecimal amt) {
        if (isLong) {
            return amt.divide(baseValue, Precision.COMMON_PRECISION, DecimalUtil.MORE).stripTrailingZeros();
        } else {
            return amt.divide(baseValue, Precision.COMMON_PRECISION, DecimalUtil.LESS).stripTrailingZeros();
        }
    }

}
