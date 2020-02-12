package com.hp.sh.expv3.bb.strategy.vo;

import java.math.BigDecimal;

/**
 * 订单的等比例数据
 * @author wangjg
 *
 */
public class OrderRatioData {
	
	//交易金额 张数*面值
	private BigDecimal amount;
	
	/**
	 * 开仓手续费,成交时修改(可能部分成交，按比例收取)
	 */
	private BigDecimal fee;

	/**
	 * 委托保证金
	 */
	private BigDecimal orderMargin;
	
	/**
	 * 总押金：委托保证金 + 开仓手续费 + 强平手续费  (以上三个字段的和：fee+closeFee+orderMargin)
	 */
	private BigDecimal grossMargin;

	public OrderRatioData() {
		super();
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal openFee) {
		this.fee = openFee;
	}
	
	public BigDecimal getOrderMargin() {
		return orderMargin;
	}

	public void setOrderMargin(BigDecimal orderMargin) {
		this.orderMargin = orderMargin;
	}

	public BigDecimal getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(BigDecimal grossMargin) {
		this.grossMargin = grossMargin;
	}
	
}
