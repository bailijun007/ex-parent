package com.hp.sh.expv3.bb.msg;

import java.math.BigDecimal;

/**
 * 订单事件消息
 * 
 * @author wangjg
 */
public class OrderEventMsg extends EventMsg {
	
	private String execId;
	
	private String tradeMatchId;
	
	private BigDecimal tradeAmt;
	
	private Integer makerFlag;

	public OrderEventMsg() {
		super();
	}

	public OrderEventMsg(Integer type, Long refId, Long time, Long userId, String asset, String symbol) {
		super(type, refId, time, userId, asset, symbol);
	}

	public String getExecId() {
		return execId;
	}

	public void setExecId(String execId) {
		this.execId = execId;
	}

	public String getTradeMatchId() {
		return tradeMatchId;
	}

	public void setTradeMatchId(String tradeMatchId) {
		this.tradeMatchId = tradeMatchId;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public Integer getMakerFlag() {
		return makerFlag;
	}

	public void setMakerFlag(Integer makerFlag) {
		this.makerFlag = makerFlag;
	}
	
}
