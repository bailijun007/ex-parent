package com.hp.sh.expv3.pc.mq.msg;

/**
 * 取消订单
 * @author lw
 *
 */
public class MatchedOrderCancelMsg extends BaseOrderMsg{
	private Long accountId;
	private Long orderId;
	
	public MatchedOrderCancelMsg() {
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
		return "MatchedOrderCancelMsg [accountId=" + accountId + ", orderId=" + orderId + ", asset=" + asset
				+ ", symbol=" + symbol + "]";
	}

}
