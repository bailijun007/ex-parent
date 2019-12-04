package com.hp.sh.expv3.pc.module.order.mq.msg;

/**
 * 订单待取消
 * @author lw
 *
 */
public class OrderPendingCancelMsg {

	private Long accountId;
	private String asset;
	private String symbol;
	private Long orderId;
	
	public OrderPendingCancelMsg(Long accountId, String asset, String symbol, Long orderId) {
		this.accountId = accountId;
		this.asset = asset;
		this.symbol = symbol;
		this.orderId = orderId;
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

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	
}
