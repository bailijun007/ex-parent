package com.hp.sh.expv3.pc.strategy.vo;

import java.math.BigDecimal;

/**
 * 一次成交的结果
 * @author wangjg
 *
 */
public class TradeResult {
	
	//此次成交的成交量
	private BigDecimal volume;
	//此次成交的成交金额
	private BigDecimal amount;
	//此次成交的成交合约价值
	private BigDecimal baseValue;
	//此次成交的手续费率
	private BigDecimal feeRatio ;
	//此次成交的手续费
	private BigDecimal fee;
	//此次成交的保证金
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
	
	//maker手续费率
	private BigDecimal makerFeeRatio;
	//maker手续费
	private BigDecimal makerFee;
	
	public TradeResult() {
		super();
	}

	//maker 手续费差额
	public BigDecimal getMakerFeeDiff(){
		if(makerFee==null){
			return BigDecimal.ZERO;
		}
		return fee.subtract(makerFee);
	}
	
	//应收手续费（考虑到maker收费优惠）
	public BigDecimal getFeeReceivable(){
		if(makerFee!=null){
			return makerFee;
		}
		return fee;
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

	public BigDecimal getMakerFee() {
		return makerFee;
	}

	public void setMakerFee(BigDecimal makerFee) {
		this.makerFee = makerFee;
	}

	public BigDecimal getMakerFeeRatio() {
		return makerFeeRatio;
	}

	public void setMakerFeeRatio(BigDecimal makerFeeRatio) {
		this.makerFeeRatio = makerFeeRatio;
	}

}
