package com.hp.sh.expv3.pc.mq.liq.msg;

import java.util.List;

import com.hp.sh.expv3.pc.msg.BaseSymbolMsg;

public class LiqCancelledMsg extends BaseSymbolMsg{

	//用户ID
    private Long accountId;    
    //仓位ID
    private Long posId;
    //多空
    private Integer longFlag;
    
    private Integer lastFlag;
    
    private List<CancelOrder> cancelOrders;
    
	public LiqCancelledMsg() {
		super();
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getPosId() {
		return posId;
	}

	public void setPosId(Long posId) {
		this.posId = posId;
	}

	public List<CancelOrder> getCancelOrders() {
		return cancelOrders;
	}

	public void setCancelOrders(List<CancelOrder> cancelOrders) {
		this.cancelOrders = cancelOrders;
	}

	public Integer getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(Integer longFlag) {
		this.longFlag = longFlag;
	}

	public Integer getLastFlag() {
		return lastFlag;
	}

	public void setLastFlag(Integer lastFlag) {
		this.lastFlag = lastFlag;
	}
	
	@Override
	public String toString() {
		return "LiqCancelledMsg [accountId=" + accountId + ", posId=" + posId + ", longFlag=" + longFlag + ", lastFlag="
				+ lastFlag + ", cancelOrders=" + cancelOrders + ", asset=" + asset + ", symbol=" + symbol + "]";
	}

	public static void main(String[] args) {
		LiqCancelledMsg msg = new LiqCancelledMsg();
		msg.setAccountId(138379937823358976L);
		msg.setPosId(138530023660355584L);
		System.out.println("收到强平撤销消息:"+msg);
	}

}
