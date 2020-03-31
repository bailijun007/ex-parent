package com.hp.sh.expv3.bb.strategy.vo;

import java.math.BigDecimal;

/**
 * 撮合成功消息
 * @author wangjg
 *
 */
public class BBTradeVo {
	
	//资产
	protected String asset;
	
	//交易对（合约品种）
	protected String symbol;
	
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

	public BBTradeVo() {
	}

	public BBTradeVo(String asset, String symbol, Long tradeId) {
		this.tradeId = tradeId;
	}
	
	public String uniqueKey(){
		return "TRADE-"+this.orderId+"-"+opponentOrderId;
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

	@Override
	public String toString() {
		return "BBTradeVo [accountId=" + accountId + ", price=" + price + ", number=" + number + ", orderId=" + orderId
				+ ", tradeId=" + tradeId + ", tradeTime=" + tradeTime + ", makerFlag=" + makerFlag + ", matchTxId="
				+ matchTxId + ", asset=" + asset + ", symbol=" + symbol + "]";
	}


}
