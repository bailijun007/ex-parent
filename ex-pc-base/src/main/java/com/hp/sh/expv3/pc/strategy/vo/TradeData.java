package com.hp.sh.expv3.pc.strategy.vo;

import java.math.BigDecimal;

/**
 * 一次成交的数据
 * @author wangjg
 *
 */
public class TradeData {
	
	//成交量
	private BigDecimal volume;
	//成交金额
	private BigDecimal amount;
	//成交合约价值
	private BigDecimal baseValue;
	//手续费率
	private BigDecimal feeRatio ;
	//手续费
	private BigDecimal fee;
	//保证金
	private BigDecimal orderMargin;
	//此次成交的盈亏（平仓才有）
	private BigDecimal pnl;

	/* 以下为累计结果 */
	
	//新均价(此次成交后，仓位的均价)
	private BigDecimal newMeanPrice;
	//强平价(此次成交后，仓位的强平价)
	private BigDecimal liqPrice;
	
	//订单完成(此次成交后，对应订单已全部成交)
	private boolean orderCompleted;

    //平均成本价：包含手续费
    private BigDecimal avgCostPrice;
	
	public TradeData() {
		super();
	}

	public BigDecimal getFeeRatio() {
		return feeRatio;
	}

	public void setFeeRatio(BigDecimal feeRatio) {
		this.feeRatio = feeRatio;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getOrderMargin() {
		return orderMargin;
	}

	public void setOrderMargin(BigDecimal margin) {
		this.orderMargin = margin;
	}

	public BigDecimal getPnl() {
		return pnl;
	}

	public void setPnl(BigDecimal pnl) {
		this.pnl = pnl;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getBaseValue() {
		return baseValue;
	}

	public void setBaseValue(BigDecimal baseValue) {
		this.baseValue = baseValue;
	}

	public BigDecimal getNewMeanPrice() {
		return newMeanPrice;
	}

	public void setNewMeanPrice(BigDecimal newMeanPrice) {
		this.newMeanPrice = newMeanPrice;
	}

	public BigDecimal getAvgCostPrice() {
		return avgCostPrice;
	}

	public void setAvgCostPrice(BigDecimal avgCostPrice) {
		this.avgCostPrice = avgCostPrice;
	}

	public BigDecimal getLiqPrice() {
		return liqPrice;
	}

	public void setLiqPrice(BigDecimal liqPrice) {
		this.liqPrice = liqPrice;
	}

	public boolean isOrderCompleted() {
		return orderCompleted;
	}

	public void setOrderCompleted(boolean completed) {
		this.orderCompleted = completed;
	}

}
