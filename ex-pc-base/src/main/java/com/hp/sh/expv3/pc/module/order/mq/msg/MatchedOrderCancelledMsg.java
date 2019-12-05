package com.hp.sh.expv3.pc.module.order.mq.msg;

import java.math.BigDecimal;

/**
 * 取消订单
 * @author lw
 *
 */
public class MatchedOrderCancelledMsg extends BaseOrderMsg{
	private Long accountId;
	private Long orderId;
	private BigDecimal cancelNumber;
	
	public MatchedOrderCancelledMsg() {
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

	public BigDecimal getCancelNumber() {
		return cancelNumber;
	}

	public void setCancelNumber(BigDecimal cancelNumber) {
		this.cancelNumber = cancelNumber;
	}

	@Override
	public String toString() {
		return "MatchedOrderCancelledMsg [accountId=" + accountId + ", orderId=" + orderId + ", cancelNumber="
				+ cancelNumber + ", asset=" + asset + ", symbol=" + symbol + "]";
	}

}
