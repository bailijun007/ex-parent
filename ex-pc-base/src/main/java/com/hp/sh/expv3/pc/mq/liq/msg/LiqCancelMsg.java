package com.hp.sh.expv3.pc.mq.liq.msg;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.mq.BaseOrderMsg;

public class LiqCancelMsg extends BaseOrderMsg{

    private Long orderId;
    private Long accountId;
    private BigDecimal cancelNumber;
    
	public LiqCancelMsg() {
		super();
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

	public BigDecimal getCancelNumber() {
		return cancelNumber;
	}

	public void setCancelNumber(BigDecimal cancelNumber) {
		this.cancelNumber = cancelNumber;
	}

}
