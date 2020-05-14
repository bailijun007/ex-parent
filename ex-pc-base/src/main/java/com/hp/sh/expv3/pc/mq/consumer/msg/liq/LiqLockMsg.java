package com.hp.sh.expv3.pc.mq.consumer.msg.liq;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.mq.consumer.msg.BaseSymbolMsg;

public class LiqLockMsg extends BaseSymbolMsg{

    private Long accountId;

    // 触发强平的标记价格
    private BigDecimal liqMarkPrice;
    
    // 触发强平的标记时间
    private Long liqMarkTime;
    
    private Integer longFlag;
    
    // 强平价
    private BigDecimal liqPrice;
    
    //仓位ID
    private Long posId;
    
	public LiqLockMsg() {
		super();
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
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

	public Integer getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(Integer longFlag) {
		this.longFlag = longFlag;
	}

	public BigDecimal getLiqPrice() {
		return liqPrice;
	}

	public void setLiqPrice(BigDecimal liqPrice) {
		this.liqPrice = liqPrice;
	}

	public Long getPosId() {
		return posId;
	}

	public void setPosId(Long posId) {
		this.posId = posId;
	}

	public String keys() {
		return ""+this.posId;
	}

	@Override
	public String toString() {
		return "LiqLockMsg [accountId=" + accountId + ", liqMarkPrice=" + liqMarkPrice + ", liqMarkTime=" + liqMarkTime
				+ ", longFlag=" + longFlag + ", liqPrice=" + liqPrice + ", asset=" + asset + ", symbol=" + symbol + "]";
	}

}
