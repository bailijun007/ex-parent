package com.hp.sh.expv3.pc.module.order.mq.msg;

/**
 * 取消订单
 * @author lw
 *
 */
public class MatchedOrderCancelMsg {
	private Long accountId;
	private String asset;
	private String symbol;
	private Long orderId;
	
	public MatchedOrderCancelMsg() {
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getAsset() {
		return asset;
	}
	public void setAsset(String asset) {
		this.asset = asset;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

}
