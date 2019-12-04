package com.hp.sh.expv3.pc.module.order.mq.msg;

/**
 * 未匹配
 * @author lw
 *
 */
public class MatchNotMatchMsg {

	private Long accountId;
	private String asset;
	private String symbol;
	private Long orderId;
	
	public MatchNotMatchMsg() {
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

	@Override
	public String toString() {
		return "MatchNotMatchMsg [accountId=" + accountId + ", asset=" + asset + ", symbol=" + symbol + ", orderId="
				+ orderId + "]";
	}
	
}
