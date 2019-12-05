package com.hp.sh.expv3.pc.module.order.mq.msg;

/**
 * 未匹配
 * @author lw
 *
 */
public class MatchNotMatchMsg extends BaseOrderMsg{

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
