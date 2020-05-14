package com.hp.sh.expv3.pc.mq.consumer.msg;

/**
 * 未匹配
 * @author lw
 *
 */
public class PcNotMatchedMsg extends BaseSymbolMsg{

	private Long accountId;
	private Long orderId;
	
	public PcNotMatchedMsg() {
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
		return "PcNotMatchedMsg [accountId=" + accountId + ", orderId=" + orderId + ", asset=" + asset + ", symbol="
				+ symbol + "]";
	}

}
