package com.hp.sh.expv3.bb.mq.msg.in;

import java.math.BigDecimal;

import com.hp.sh.expv3.bb.mq.msg.BaseSymbolMsg;

/**
 * 取消订单
 * @author lw
 *
 */
public class BBCancelledMsg extends BaseSymbolMsg{
    private Long accountId;
    private Long orderId;
    private BigDecimal cancelNumber;
    
    private Long seqId;
	
	public BBCancelledMsg() {
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

	public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}

	@Override
	public String toString() {
		return "BBCancelledMsg [accountId=" + accountId + ", orderId=" + orderId + ", cancelNumber="
				+ cancelNumber + ", asset=" + asset + ", symbol=" + symbol + "]";
	}

}
