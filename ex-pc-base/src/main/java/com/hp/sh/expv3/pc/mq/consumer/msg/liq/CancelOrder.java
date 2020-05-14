package com.hp.sh.expv3.pc.mq.consumer.msg.liq;

import java.math.BigDecimal;

public class CancelOrder {
	private Long orderId;
	private BigDecimal cancelNumber;
	
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
		return "CancelOrder [orderId=" + orderId + ", cancelNumber=" + cancelNumber + "]";
	}
	
}
