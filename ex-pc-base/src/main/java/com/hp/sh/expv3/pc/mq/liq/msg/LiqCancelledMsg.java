package com.hp.sh.expv3.pc.mq.liq.msg;

import java.util.List;

import com.hp.sh.expv3.pc.mq.BaseOrderMsg;

public class LiqCancelledMsg extends BaseOrderMsg{

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

}
