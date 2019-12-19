package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;

/**
 * AABB持仓计算测率
 * @author wangjg
 *
 */
public class AABBHoldPosStrategy implements HoldPosStrategy{
	
	/**
	 * 当前仓位的开仓均价
	 * @return
	 */
	public BigDecimal getPosMeanPrice(){
		
		return null;
	}

	/**
	 * 当前保证金率
	 * @return
	 */
	public BigDecimal getPosMarginRatio(){
		
		return null;
	}
	
	/**
	 * 当前仓位的未实现盈亏(浮动盈亏)
	 * @return
	 */
	public BigDecimal getPosPnl(){
		
		return null;
	}

	/**
	 * 当前仓位的强平价(预估强平价)
	 * @return
	 */
	public BigDecimal getPosLiqPrice(){
		
		return null;
	}

}
