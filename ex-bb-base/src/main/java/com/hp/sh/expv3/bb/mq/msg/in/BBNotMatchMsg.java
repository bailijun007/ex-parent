package com.hp.sh.expv3.bb.mq.msg.in;

import com.hp.sh.expv3.bb.mq.msg.BaseSymbolMsg;

/**
 * 未匹配
 * @author lw
 *
 */
public class BBNotMatchMsg extends BaseSymbolMsg{

	private Long accountId;
	private Long orderId;
	
	public BBNotMatchMsg() {
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
		return "BBNotMatchMsg [accountId=" + accountId + ", orderId=" + orderId + ", asset=" + asset + ", symbol="
				+ symbol + "]";
	}

}