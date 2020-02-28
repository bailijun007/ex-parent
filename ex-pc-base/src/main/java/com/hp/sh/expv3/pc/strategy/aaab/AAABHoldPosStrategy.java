package com.hp.sh.expv3.pc.strategy.aaab;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.pc.strategy.aabb.AABBCompFieldCalc;
import com.hp.sh.expv3.pc.strategy.data.PosData;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.DecimalUtil;
import com.hp.sh.expv3.utils.math.Precision;

/**
 * AABB持仓计算策略
 * @author wangjg
 *
 */
public class AAABHoldPosStrategy implements HoldPosStrategy{
	
	/**
	 * 计算收益
	 * @param longFlag 是否多仓
	 * @param amt 成交金额
	 * @param openPrice 均价
	 * @param closePrice 平仓价
	 * @return
	 */
	@Override
	public BigDecimal calcPnl(int longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal openPrice, BigDecimal closePrice) {
		BigDecimal _amount = volume.multiply(faceValue);
        return PnlCalc.calcPnl(longFlag, _amount, openPrice, closePrice);
    }
	
	/**
	 * 成交均价
	 * @param isLong
	 * @param baseValue
	 * @param amt
	 * @return
	 */
	@Override
    public BigDecimal calcMeanPrice(int longFlag, BigDecimal baseValue, BigDecimal amt) {
        return PcPriceCalc.calcEntryPrice(IntBool.isTrue(longFlag), baseValue, amt);
    }
	
	@Override
	public BigDecimal calcLiqPrice(PosData pos) {
		BigDecimal amount = AABBCompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		return PcPriceCalc.calcLiqPrice( pos.getHoldMarginRatio(), IntBool.isTrue(pos.getLongFlag()), pos.getMeanPrice(), amount, pos.getPosMargin(), Precision.COMMON_PRECISION );
	}
	
	/**
	 * 计算仓位的强平价(预估强平价)
	 * @param longFlag 多/空
	 * @param amount 持仓金额
	 * @param meanPrice 均价 
	 * @param holdMarginRatio 维持保证金率
	 * @param posMargin 保证金
	 * @return 强平价
	 */
	@Override
	public BigDecimal calcLiqPrice(int longFlag, BigDecimal amount, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		return PcPriceCalc.calcLiqPrice( holdMarginRatio, IntBool.isTrue(longFlag), openPrice, amount, posMargin, Precision.COMMON_PRECISION );
	}
	
	/**
	 * 计算仓位保证金率
	 * 保证金率 =（保证金+未实现盈亏) / (合约面值*持仓张数／最新标记价格）
	 * @return
	 */
	@Override
	public BigDecimal calPosMarginRatio(BigDecimal posMargin, BigDecimal posPnl, BigDecimal faceValue, BigDecimal volume, BigDecimal markPrice){
//		BigDecimal marginRatio = posMargin.add(posPnl).divide((faceValue.multiply(volume).divide(markPrice)));
		BigDecimal marginRatio = posMargin.add(posPnl).multiply(markPrice).divide(faceValue.multiply(volume), Precision.COMMON_PRECISION, DecimalUtil.LESS);
		
		return marginRatio;
	}

	/**
	 * 用 均价 标记价格 未实现盈亏 计算 仓位保证金
	 * @param longFlag
	 * @param initMarginRatio
	 * @param amount
	 * @param meanPrice
	 * @param markPrice
	 * @return
	 */
	@Override
	public BigDecimal calcInitMargin(Integer longFlag, BigDecimal initMarginRatio, BigDecimal volume, BigDecimal faceValue, BigDecimal meanPrice, BigDecimal markPrice) {
        // ( 1 / leverage ) * volume = volume / leverage
		BigDecimal amount = volume.multiply(faceValue).multiply(meanPrice);
        BigDecimal pnl = PnlCalc.calcPnl(longFlag, amount, meanPrice, markPrice);
        BigDecimal baseValue = AABBCompFieldCalc.calcBaseValue(amount, meanPrice);
        return baseValue.multiply(initMarginRatio).subtract(pnl.min(BigDecimal.ZERO)).stripTrailingZeros();
	}
	
	/**
	 * 计算破产价
	 * @param isLong
	 * @param openPrice
	 * @param amt
	 * @param margin
	 * @return
	 */
	@Override
	public BigDecimal calcBankruptPrice(Integer longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal margin, BigDecimal openPrice) {
		BigDecimal _amount = AABBCompFieldCalc.calcAmount(faceValue, volume);
		return PcPriceCalc.calcBankruptPrice(IntBool.isTrue(longFlag), openPrice, _amount, margin, Precision.COMMON_PRECISION );
	}
}
