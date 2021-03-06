package com.hp.sh.expv3.pc.mq.consumer.msg.liq;

import java.math.BigDecimal;
import java.util.List;

import com.hp.sh.expv3.pc.mq.consumer.msg.BaseSymbolMsg;

public class LiqCancelledMsg extends BaseSymbolMsg{

	//用户ID
    private Long accountId;    
    //仓位ID
    private Long posId;
    //多空
    private Integer longFlag;
    
    private Integer lastFlag;
    
    /**
     * 触发强平的标记价格
     */
    private BigDecimal liqMarkPrice;

    /**
     * 触发强平的标记时间
     */
    private Long liqMarkTime;
    
    private List<CancelOrder> cancelOrders;
    
    private Long seqId;
    
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
	
	public BigDecimal getLiqMarkPrice() {
		return liqMarkPrice;
	}

	public void setLiqMarkPrice(BigDecimal liqMarkPrice) {
		this.liqMarkPrice = liqMarkPrice;
	}

	public Long getLiqMarkTime() {
		return liqMarkTime;
	}

	public void setLiqMarkTime(Long liqMarkTime) {
		this.liqMarkTime = liqMarkTime;
	}

	public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
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
