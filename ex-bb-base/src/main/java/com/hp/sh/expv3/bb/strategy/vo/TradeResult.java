package com.hp.sh.expv3.bb.strategy.vo;

import java.math.BigDecimal;

/**
 * 一次成交的结果
 * @author wangjg
 *
 */
public class TradeResult {
	
	//此次成交的成交量
	private BigDecimal tradeVolume;
	//此次成交的成交价
	private BigDecimal tradePrice;
	//此次成交的成交金额
	private BigDecimal tradeAmount;
	//此次成交的手续费率
	private BigDecimal tradeFeeRatio ;
	//此次成交的手续费
	private BigDecimal tradeFee;
	//此次成交扣除的保证金
	private BigDecimal tradeOrderMargin;
	
	/* 以下为订单累计结果 */
	
	//订单是否完成(此次成交后，对应订单已全部成交)
	private boolean isOrderCompleted;
	//此次成交的成交量
	private BigDecimal remainVolume;
	
	public TradeResult() {
		super();
	}

	public BigDecimal getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(BigDecimal price) {
		this.tradePrice = price;
	}

	public BigDecimal getTradeFeeRatio() {
		return tradeFeeRatio;
	}

	public void setTradeFeeRatio(BigDecimal feeRatio) {
		this.tradeFeeRatio = feeRatio;
	}

	public BigDecimal getTradeFee() {
		return tradeFee;
	}

	public void setTradeFee(BigDecimal fee) {
		this.tradeFee = fee;
	}

	public BigDecimal getTradeOrderMargin() {
		return tradeOrderMargin;
	}

	public void setTradeOrderMargin(BigDecimal margin) {
		this.tradeOrderMargin = margin;
	}

	public BigDecimal getTradeVolume() {
		return tradeVolume;
	}

	public void setTradeVolume(BigDecimal volume) {
		this.tradeVolume = volume;
	}

	public boolean isOrderCompleted() {
		return isOrderCompleted;
	}

	public void setOrderCompleted(boolean completed) {
		this.isOrderCompleted = completed;
	}

	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(BigDecimal amount) {
		this.tradeAmount = amount;
	}
	
	public BigDecimal getRemainVolume() {
		return remainVolume;
	}

	public void setRemainVolume(BigDecimal remainVolume) {
		this.remainVolume = remainVolume;
	}

}
