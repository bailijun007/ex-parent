package com.hp.sh.expv3.bb.mq.msg.in;

import java.math.BigDecimal;

import com.hp.sh.expv3.bb.mq.msg.BaseSymbolMsg;

/**
 * 撮合成功消息
 * @author wangjg
 *
 */
public class BBTradeMsg extends BaseSymbolMsg{

	//用户ID
	private Long accountId;
	
	/* 成交价格 */
	private BigDecimal price;

	/* 成交数量 */
	private BigDecimal number;

	//订单Id
	private Long orderId;

	//交易ID
	private Long tradeId;

	//成交时间
	private Long tradeTime;

	//是否maker
	private Integer makerFlag;
	
	//撮合事务Id
	private Long matchTxId;
	
	//对手订单ID
	private Long opponentOrderId;
	
    private Long seqId;

	public BBTradeMsg() {
	}

	public BBTradeMsg(String asset, String symbol, Long tradeId) {
		this.tradeId = tradeId;
	}
	
	public String uniqueKey(){
		return "TRADE_"+this.orderId+"_"+opponentOrderId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public void setNumber(BigDecimal number) {
		this.number = number;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public Long getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Long tradeTime) {
		this.tradeTime = tradeTime;
	}

	public Integer getMakerFlag() {
		return makerFlag;
	}

	public void setMakerFlag(Integer makerFlag) {
		this.makerFlag = makerFlag;
	}

	public Long getMatchTxId() {
		return matchTxId;
	}

	public void setMatchTxId(Long matchTxId) {
		this.matchTxId = matchTxId;
	}

	public Long getOpponentOrderId() {
		return opponentOrderId;
	}

	public void setOpponentOrderId(Long opponentOrderId) {
		this.opponentOrderId = opponentOrderId;
	}

	public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}

	@Override
	public String toString() {
		return "BBTradeMsg [asset=" + asset + ", symbol=" + symbol + ", accountId=" + accountId + ", price=" + price
				+ ", number=" + number + ", orderId=" + orderId + ", tradeId=" + tradeId + ", tradeTime=" + tradeTime
				+ ", makerFlag=" + makerFlag + ", matchTxId=" + matchTxId + ", opponentOrderId=" + opponentOrderId
				+ ", topic=" + topic + ", tags=" + tags + ", keys=" + keys + ", msgId=" + msgId + ", uniqueKey="+this.uniqueKey()+"]";
	}


}
