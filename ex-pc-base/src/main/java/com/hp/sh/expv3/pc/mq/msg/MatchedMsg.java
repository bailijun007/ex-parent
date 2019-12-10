package com.hp.sh.expv3.pc.mq.msg;

import java.math.BigDecimal;

/**
 * 撮合成功消息
 * @author wangjg
 *
 */
public class MatchedMsg extends BaseOrderMsg {
	
	//用户ID
	private Long accountId;
	
	//资产
	private String asset;
	
	//交易对（合约品种）
	private String symbol;

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

	public MatchedMsg() {
	}

	public MatchedMsg(String asset, String symbol, Long tradeId) {
		this.asset = asset;
		this.symbol = symbol;
		this.tradeId = tradeId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	@Override
	public String toString() {
		return "MatchedMsg [accountId=" + accountId + ", asset=" + asset + ", symbol=" + symbol + ", price=" + price
				+ ", number=" + number + ", orderId=" + orderId + ", tradeId=" + tradeId + ", tradeTime=" + tradeTime
				+ ", makerFlag=" + makerFlag + ", matchTxId=" + matchTxId + "]";
	}

}
