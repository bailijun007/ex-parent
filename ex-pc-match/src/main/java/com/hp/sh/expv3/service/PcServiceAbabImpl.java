package com.hp.sh.expv3.service;

import com.hp.sh.expv3.match.util.DecimalUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * asset : A
 * symbol : B_A
 * contract : nB
 * <p>
 * example : USDT -> BTC_USDT , 每张合约面值 0.005 BTC
 * <p>
 * case : ABAB
 * gmex: USDT -> BTC_USDT ; gate USDT -> BTC_USDT
 */
@Service
public class PcServiceAbabImpl {

    /**
     * 求强平价：即在 强平价时 平仓，收回的保证金占整个仓位价值 的比率 = 维持仓位保证金（纯粹的仓位维持保证金率，不包括其他的手续费率）
     *
     * @return
     */
    public BigDecimal calcLiqPrice(BigDecimal posVolume, BigDecimal faceValue, BigDecimal posMargin, BigDecimal posBaseValue, BigDecimal holdRatio, int scale) {
        // TODO
        return null;
    }

    /**
     * 计算盈亏
     *
     * @param isLong     是否多仓
     * @param posVolume  平仓张数
     * @param meanPrice  开仓均价
     * @param closePrice 平仓价
     * @return
     */
    public BigDecimal calcPnl(boolean isLong, BigDecimal posVolume, BigDecimal faceValue, BigDecimal meanPrice, BigDecimal closePrice, int scale) {
        // TODO
        return null;
    }

    /**
     * 计算破产价，即所有保证金全部亏损
     *
     * @param posVolume    持仓数量
     * @param faceValue    面值
     * @param posMargin    仓位保证金
     * @param posBaseValue 仓位基础货币价值
     * @param scale        精度
     * @return
     */
    public BigDecimal calcBankruptPrice(BigDecimal posVolume, BigDecimal faceValue, BigDecimal posMargin, BigDecimal posBaseValue, int scale) {
        // TODO
        return null;
    }

    /**
     * 计算破产价，即所有保证金全部亏损
     *
     * @param posVolume    持仓数量
     * @param faceValue    面值
     * @param posMargin    仓位保证金
     * @param scale        精度
     * @param posMeanPrice 仓位基础货币价值
     * @return
     */
    public BigDecimal calcBankruptPrice(BigDecimal posVolume, BigDecimal faceValue, BigDecimal posMargin, int scale, BigDecimal posMeanPrice) {
        // TODO
        return null;
    }

    /**
     * abaa, USD -> BTC_USD ，每张合约 xUSD
     * 计算持仓均价
     *
     * @param faceValue     面值
     * @param currentPrice  本次成交价格
     * @param currentVolume 本次成交合约张数，平仓是减少持仓数量，传负数
     * @param prevVolume    已持仓合约张数，首次建仓则已有仓位为0
     * @param prevBaseValue 已有基础货币价值，
     * @param scale         精度
     * @return
     */
    public BigDecimal calcMeanPrice(BigDecimal faceValue, BigDecimal currentPrice, BigDecimal currentVolume, BigDecimal prevVolume, BigDecimal prevBaseValue, int scale) {
        // TODO
        return null;
    }

    /**
     * 计算基础货币价值
     *
     * @param volume    合约张数
     * @param faceValue 合约面值
     * @return
     */
    public BigDecimal calcBaseValue(BigDecimal volume, BigDecimal faceValue) {
        return volume.multiply(faceValue);
    }

    /**
     * 计算结算货币价值
     *
     * @param volume    合约张数
     * @param faceValue 面值
     * @return
     */
    public BigDecimal calcSettleValue(BigDecimal volume, BigDecimal faceValue, BigDecimal price, int scale) {
        return volume.multiply(faceValue).divide(price, scale, DecimalUtil.LESS);
    }


}