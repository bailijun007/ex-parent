package com.hp.sh.expv3.service;

import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.util.DecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * asset : A
 * symbol : B_A
 * contract : nA
 * <p>
 * example : USDT -> BTC_USDT , 每张合约面值 1 USDT，代表 价值1 USDT 的BTC
 * <p>
 * case : ABAA
 */
@Service
public class PcPriceServiceAbaaImpl {

    @Autowired
    private PcVolumeService pcVolumeService;

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
    public BigDecimal calcLiqPrice(BigDecimal posHoldMarginRatio, boolean isLong, BigDecimal openPrice, BigDecimal amt, BigDecimal posMargin, int scale) {
        BigDecimal holdVolume = pcVolumeService.calcVolume(amt, openPrice, scale);
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
            return amt.multiply(posHoldMarginRatio.add(BigDecimal.ONE)).divide(posMargin.add(holdVolume), CommonConst.COMMON_PRECISION, DecimalUtil.MORE).max(BigDecimal.ZERO).stripTrailingZeros();
        } else {
            /**
             * ( posMargin + pnl ) / ( amt / op ) = posHoldMarginRatio
             * ( posMargin + (amt /cp ) - (amt/op) ) / ( amt / cp ) = posHoldMarginRatio
             * [ posMargin - (amt/op) + (amt /cp) ] / ( amt / cp ) = posHoldMarginRatio
             * [ posMargin + (amt/cp) - (amt/op)  ] / ( amt / cp ) = posHoldMarginRatio
             * [( posMargin - (amt/op) ) / ( amt / cp ) ] + 1 = posHoldMarginRatio
             * [( posMargin - (amt/op) ) / ( amt / cp ) ] = posHoldMarginRatio - 1
             * [( posMargin - (amt/op) ) / (posHoldMarginRatio - 1) ] = ( amt / cp )
             * cp = amt * (posHoldMarginRatio - 1) / (posMargin - v)
             */
            return amt.multiply(posHoldMarginRatio.subtract(BigDecimal.ONE)).divide(posMargin.subtract(holdVolume), CommonConst.COMMON_PRECISION, DecimalUtil.MORE).max(BigDecimal.ZERO).stripTrailingZeros();
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
    public BigDecimal calcBankruptPrice(boolean isLong, BigDecimal openPrice, BigDecimal amt, BigDecimal margin, int scale) {
        BigDecimal volume = pcVolumeService.calcVolume(amt, openPrice, scale);
        if (isLong) {
            return amt.divide(volume.add(margin), scale, DecimalUtil.MORE).max(BigDecimal.ZERO).stripTrailingZeros();
        } else {
            return amt.divide(volume.subtract(margin), scale, DecimalUtil.LESS).max(BigDecimal.ZERO).stripTrailingZeros();
        }
    }

    /**
     * 计算持仓均价
     *
     * @param isLong
     * @param volume
     * @param amt
     * @return
     */
    public BigDecimal calcEntryPrice(boolean isLong, BigDecimal volume, BigDecimal amt, int scale) {
        if (isLong) {
            return amt.divide(volume, scale, DecimalUtil.MORE).stripTrailingZeros();
        } else {
            return amt.divide(volume, scale, DecimalUtil.LESS).stripTrailingZeros();
        }
    }

}
