package com.hp.sh.expv3.bb.mq.match.msg;

import com.hp.sh.expv3.bb.msg.BaseSymbolMsg;

/**
 * 未匹配
 * @author lw
 *
 */
public class MatchNotMatchMsg extends BaseSymbolMsg{

	private Long accountId;
	private Long orderId;
	
	public MatchNotMatchMsg() {
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
		return "MatchNotMatchMsg [accountId=" + accountId + ", orderId=" + orderId + ", asset=" + asset + ", symbol="
				+ symbol + "]";
	}

}
