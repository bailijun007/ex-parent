package com.hp.sh.expv3.pc.strategy.vo;

import java.math.BigDecimal;

/**
 * 成交仓位结果
 * @author wangjg
 *
 */
public class PosTradeResult {

	/* 以下为累计结果 */
	
	//新仓位价值
	private BigDecimal newPosBaseValue;
	
	//新仓位均价(此次成交后，仓位的均价)
	private BigDecimal newPosMeanPrice;
	//新预估强平价(此次成交后，仓位的强平价)
	private BigDecimal newPosLiqPrice;
	
	//订单完成(此次成交后，对应订单已全部成交)
	private boolean isOrderCompleted;

	public PosTradeResult() {
	}

	public BigDecimal getNewPosBaseValue() {
		return newPosBaseValue;
	}

	public void setNewPosBaseValue(BigDecimal newPosBaseValue) {
		this.newPosBaseValue = newPosBaseValue;
	}

	public BigDecimal getNewPosMeanPrice() {
		return newPosMeanPrice;
	}

	public void setNewPosMeanPrice(BigDecimal newPosMeanPrice) {
		this.newPosMeanPrice = newPosMeanPrice;
	}

	public BigDecimal getNewPosLiqPrice() {
		return newPosLiqPrice;
	}

	public void setNewPosLiqPrice(BigDecimal newPosLiqPrice) {
		this.newPosLiqPrice = newPosLiqPrice;
	}

	public boolean isOrderCompleted() {
		return isOrderCompleted;
	}

	public void setOrderCompleted(boolean isOrderCompleted) {
		this.isOrderCompleted = isOrderCompleted;
	}
	
}
