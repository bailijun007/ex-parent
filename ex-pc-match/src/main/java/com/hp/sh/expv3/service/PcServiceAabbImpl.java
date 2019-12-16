package com.hp.sh.expv3.service;

import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.util.DecimalUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * asset : A
 * symbol : A_B
 * contract : nB
 * <p>
 * example : BTC -> BTC_USD , 每张合约面值 1 USD，代表 价值1USD 的BTC
 * <p>
 * case : AABB
 * bitmex BTC -> BTC_USD 1USD
 */
@Service
public class PcServiceAabbImpl {

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
        BigDecimal holdVolume = calcBaseValue(amt, openPrice, scale);
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
     * 计算开仓均价
     *
     * @param isLong       是否多仓
     * @param posBaseValue 仓位基础货币价值
     * @param amt          仓位计价货币价值
     * @param scale        精度
     * @return
     */
    public BigDecimal calcMeanPrice(boolean isLong, BigDecimal posBaseValue, BigDecimal amt, int scale) {
        return amt.divide(posBaseValue, scale, DecimalUtil.LESS).stripTrailingZeros();
    }

    /**
     * 计算盈亏
     *
     * @param longFlag
     * @param amt
     * @param meanPrice
     * @param closePrice
     * @param scale
     * @return
     */
    public BigDecimal calcPnl(int longFlag, BigDecimal amt, BigDecimal meanPrice, BigDecimal closePrice, int scale) {
        return calcPnl(CommonConst.isYes(longFlag), amt, meanPrice, calcBaseValue(amt, closePrice, scale), scale);
    }

    /**
     * 计算盈亏
     *
     * @param amt            计价金额
     * @param meanPrice      开仓均价
     * @param closeBaseValue 平仓基础货币价值
     * @param isLong         是否多仓
     * @param scale          精度
     * @return
     */
    public BigDecimal calcPnl(boolean isLong, BigDecimal amt, BigDecimal meanPrice, BigDecimal closeBaseValue, int scale) {
        return calcPnl(isLong, calcBaseValue(amt, meanPrice, scale), closeBaseValue);
    }

    /**
     * 计算盈亏
     *
     * @param openBaseValue  开仓基础货币价值
     * @param closeBaseValue 平仓基础货币价值
     * @param isLong         是否多仓
     * @return
     */
    public BigDecimal calcPnl(boolean isLong, BigDecimal openBaseValue, BigDecimal closeBaseValue) {
        BigDecimal pnl = closeBaseValue.subtract(openBaseValue);
        return (isLong ? pnl.negate() : pnl).stripTrailingZeros();
    }

    /**
     * 计算基础货币价值
     *
     * @param volume    合约张数
     * @param faceValue 合约面值
     * @param price     价格
     * @param scale     精度
     * @return
     */
    public BigDecimal calcBaseValue(BigDecimal volume, BigDecimal faceValue, BigDecimal price, int scale) {
        return calcBaseValue(calcAmt(volume, faceValue), price, scale).stripTrailingZeros();
    }

    /**
     * 计算基础货币价值
     *
     * @param amt   计价金额
     * @param price 价格
     * @param scale 精度
     * @return
     */
    public BigDecimal calcBaseValue(BigDecimal amt, BigDecimal price, int scale) {
        return amt.divide(price, scale, DecimalUtil.LESS).stripTrailingZeros();
    }

    /**
     * 计算 计价金额
     *
     * @param volume    合约张数
     * @param faceValue 合约面值
     * @return
     */
    public BigDecimal calcAmt(BigDecimal volume, BigDecimal faceValue) {
        return volume.multiply(faceValue);
    }

    /**
     * 计算破产价，即所有保证金全部亏损
     *
     * @param isLong    是否多仓
     * @param openPrice 开仓均价
     * @param amt       计价金额
     * @param posMargin 仓位保证金
     * @return
     */
    public BigDecimal calcBankruptPrice(boolean isLong, BigDecimal openPrice, BigDecimal amt, BigDecimal posMargin, int scale) {
        BigDecimal volume = calcBaseValue(amt, openPrice, scale);
        if (isLong) {
            return amt.divide(volume.add(posMargin), scale, DecimalUtil.MORE).max(BigDecimal.ZERO).stripTrailingZeros();
        } else {
            return amt.divide(volume.subtract(posMargin), scale, DecimalUtil.LESS).max(BigDecimal.ZERO).stripTrailingZeros();
        }
    }

}
