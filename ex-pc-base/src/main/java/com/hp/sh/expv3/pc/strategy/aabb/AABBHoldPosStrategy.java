package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.calc.CompFieldCalc;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.DecimalUtil;

/**
 * AABB持仓计算策略
 * @author wangjg
 *
 */
@Component
public class AABBHoldPosStrategy implements HoldPosStrategy{
	
	/**
	 * 计算仓位的开仓均价
	 * @return
	 */
	public BigDecimal calPosMeanPrice(){
		
		return null;
	}

	/**
	 * 计算仓位保证金率
	 * 保证金率 =（保证金+未实现盈亏) / (合约面值*持仓张数／最新标记价格）
	 * @return
	 */
	public BigDecimal calPosMarginRatio(BigDecimal posMargin, BigDecimal posPnl, BigDecimal faceValue, BigDecimal volume, BigDecimal markPrice){
//		BigDecimal marginRatio = posMargin.add(posPnl).divide((faceValue.multiply(volume).divide(markPrice)));
		BigDecimal marginRatio = posMargin.add(posPnl).multiply(markPrice).divide(faceValue.multiply(volume), Precision.COMMON_PRECISION, DecimalUtil.LESS);
		
		return marginRatio;
	}
	
	/**
	 * 计算成交收益
	 * @param longFlag 
	 * @param amount 交易金额
	 * @param meanPrice 开仓均价
	 * @param price 成交价
	 * @return
	 */
	public BigDecimal calcTradePnl(int longFlag, BigDecimal amount, BigDecimal meanPrice, BigDecimal price){
		BigDecimal pnl = PnlCalc.calcPnl(longFlag, amount, meanPrice, price);
		return pnl;
	}
	
	/**
	 * 计算仓位的未实现盈亏(浮动盈亏)
	 * @return
	 */
	public BigDecimal calcPosPnl(int longFlag, BigDecimal amount, BigDecimal meanPrice, BigDecimal markPrice){
		BigDecimal pnl = PnlCalc.calcPnl(longFlag, amount, meanPrice, markPrice);
		return pnl;
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
	public BigDecimal calcPosLiqPrice(int longFlag, BigDecimal amount, BigDecimal meanPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		return PcPriceCalc.calcLiqPrice(holdMarginRatio, IntBool.isTrue(longFlag), meanPrice, amount, posMargin, Precision.COMMON_PRECISION );
	}
	
	/**
	 * 用 均价 标记价格 未实现盈亏 计算 仓位保证金
	 * @param longFlag
	 * @param initMarginRatio
	 * @param amount
	 * @param feeRatio
	 * @param meanPrice
	 * @param markPrice
	 * @return
	 */
	public BigDecimal calcMarginWhenChangeLeverage(Integer longFlag, BigDecimal initMarginRatio, BigDecimal amount, BigDecimal feeRatio, BigDecimal meanPrice, BigDecimal markPrice) {
        // ( 1 / leverage ) * volume = volume / leverage
        BigDecimal pnl = PnlCalc.calcPnl(longFlag, amount, meanPrice, markPrice);
        BigDecimal baseValue = CompFieldCalc.calcBaseValue(amount, meanPrice);
        return baseValue.multiply(initMarginRatio).subtract(pnl.min(BigDecimal.ZERO)).stripTrailingZeros();
	}
	
}
