package com.hp.sh.expv3.bb.strategy.vo;

import java.math.BigDecimal;

/**
 * 一次成交的结果
 * @author wangjg
 *
 */
public class TradeResult {
	
	//此次成交的成交量
	private BigDecimal volume;
	//此次成交的成交价
	private BigDecimal price;
	//此次成交的成交金额
	private BigDecimal amount;
	//此次成交的成交合约价值
	private BigDecimal baseValue;
	//此次成交的手续费率
	private BigDecimal feeRatio ;
	//此次成交的手续费
	private BigDecimal fee;
	//此次成交的maker手续费率
	private BigDecimal makerFeeRatio;
	//此次成交的taker手续费
	private BigDecimal makerFee;
	//此次支付的保证金
	private BigDecimal orderMargin;
	//此次成交的盈亏（平仓才有）
	private BigDecimal pnl;
	
	/* 以下为订单累计结果 */
	
	//订单是否完成(此次成交后，对应订单已全部成交)
	private boolean isOrderCompleted;
	//此次成交的成交量
	private BigDecimal remainVolume;
	//订单剩余保证金
	private BigDecimal remainOrderMargin;
	//订单剩余手续费
	private BigDecimal remainFee;

	/* 以下为仓位累计结果 */
	
	//新仓位价值
	private BigDecimal newPosBaseValue;
	
	//新仓位均价(此次成交后，仓位的均价)
	private BigDecimal newPosMeanPrice;
	//新预估强平价(此次成交后，仓位的强平价)
	private BigDecimal newPosLiqPrice;
	
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
	public BigDecimal getReceivableFee(){
		if(makerFee!=null){
			return makerFee;
		}
		return fee;
	}
	
	//应收手续费率（考虑到maker收费优惠）
	public BigDecimal getReceivableFeeRatio(){
		if(makerFeeRatio!=null){
			return makerFeeRatio;
		}
		return feeRatio;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
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

	public BigDecimal getBaseValue() {
		return baseValue;
	}

	public void setBaseValue(BigDecimal baseValue) {
		this.baseValue = baseValue;
	}

	public BigDecimal getNewPosMeanPrice() {
		return newPosMeanPrice;
	}

	public void setNewPosMeanPrice(BigDecimal newMeanPrice) {
		this.newPosMeanPrice = newMeanPrice;
	}

	public BigDecimal getNewPosLiqPrice() {
		return newPosLiqPrice;
	}

	public void setNewPosLiqPrice(BigDecimal liqPrice) {
		this.newPosLiqPrice = liqPrice;
	}

	public boolean getOrderCompleted() {
		return isOrderCompleted;
	}

	public void setOrderCompleted(boolean completed) {
		this.isOrderCompleted = completed;
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

	public BigDecimal getNewPosBaseValue() {
		return newPosBaseValue;
	}

	public void setNewPosBaseValue(BigDecimal newPosBaseValue) {
		this.newPosBaseValue = newPosBaseValue;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getRemainOrderMargin() {
		return remainOrderMargin;
	}

	public void setRemainOrderMargin(BigDecimal remainOrderMargin) {
		this.remainOrderMargin = remainOrderMargin;
	}

	public BigDecimal getRemainFee() {
		return remainFee;
	}

	public void setRemainFee(BigDecimal remainFee) {
		this.remainFee = remainFee;
	}

	public BigDecimal getRemainVolume() {
		return remainVolume;
	}

	public void setRemainVolume(BigDecimal remainVolume) {
		this.remainVolume = remainVolume;
	}

}
