package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.utils.IntBool;

/**
 * AABB持仓计算测率
 * @author wangjg
 *
 */
public class AABBHoldPosStrategy implements HoldPosStrategy{
	
	/**
	 * 计算仓位的开仓均价
	 * @return
	 */
	public BigDecimal getPosMeanPrice(){
		
		return null;
	}

	/**
	 * 计算保证金率
	 * @return
	 */
	public BigDecimal getPosMarginRatio(BigDecimal posMargin, BigDecimal posPnl, BigDecimal faceValue, BigDecimal volume, BigDecimal markPrice){
		
		return null;
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

}
