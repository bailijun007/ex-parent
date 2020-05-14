package com.hp.sh.expv3.pc.mq.consumer.msg;

import com.hp.sh.expv3.pc.msg.BaseSymbolMsg;

/**
 * 订单待取消
 * @author lw
 *
 */
public class OrderPendingCancelMsg extends BaseSymbolMsg{

	private Long accountId;
	private Long orderId;
	
	public OrderPendingCancelMsg(Long accountId, String asset, String symbol, Long orderId) {
		this.accountId = accountId;
		this.asset = asset;
		this.symbol = symbol;
		this.orderId = orderId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "OrderPendingCancelMsg [accountId=" + accountId + ", orderId=" + orderId + ", asset=" + asset
				+ ", symbol=" + symbol + "]";
	}
	
}
