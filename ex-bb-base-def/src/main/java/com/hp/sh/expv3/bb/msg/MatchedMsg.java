package com.hp.sh.expv3.bb.msg;

import java.math.BigDecimal;

/**
 * 成交
 * @author wangjg
 *
 */
public class MatchedMsg {

	//资产
	protected String asset;
	
	//交易对（合约品种）
	protected String symbol;
	
	private Long id;
	private Long matchTxId;
	private Long mkAccountId;
	private Long mkCloseFlag;
	private Long mkOrderId;
	private BigDecimal number;
	private BigDecimal price;
	private Long tkAccountId;
	private Long tkBidFlag;
	private Long tkCloseFlag;
	private Long tkOrderId;
	private Long tradeTime;
	
	public MatchedMsg() {
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMatchTxId() {
		return matchTxId;
	}
	public void setMatchTxId(Long matchTxId) {
		this.matchTxId = matchTxId;
	}
	public Long getMkAccountId() {
		return mkAccountId;
	}
	public void setMkAccountId(Long mkAccountId) {
		this.mkAccountId = mkAccountId;
	}
	public Long getMkCloseFlag() {
		return mkCloseFlag;
	}
	public void setMkCloseFlag(Long mkCloseFlag) {
		this.mkCloseFlag = mkCloseFlag;
	}
	public Long getMkOrderId() {
		return mkOrderId;
	}
	public void setMkOrderId(Long mkOrderId) {
		this.mkOrderId = mkOrderId;
	}
	public BigDecimal getNumber() {
		return number;
	}
	public void setNumber(BigDecimal number) {
		this.number = number;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Long getTkAccountId() {
		return tkAccountId;
	}
	public void setTkAccountId(Long tkAccountId) {
		this.tkAccountId = tkAccountId;
	}
	public Long getTkBidFlag() {
		return tkBidFlag;
	}
	public void setTkBidFlag(Long tkBidFlag) {
		this.tkBidFlag = tkBidFlag;
	}
	public Long getTkCloseFlag() {
		return tkCloseFlag;
	}
	public void setTkCloseFlag(Long tkCloseFlag) {
		this.tkCloseFlag = tkCloseFlag;
	}
	public Long getTkOrderId() {
		return tkOrderId;
	}
	public void setTkOrderId(Long tkOrderId) {
		this.tkOrderId = tkOrderId;
	}
	public Long getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(Long tradeTime) {
		this.tradeTime = tradeTime;
	}

	@Override
	public String toString() {
		return "MatchedMsg [id=" + id + ", matchTxId=" + matchTxId + ", mkAccountId=" + mkAccountId + ", mkCloseFlag="
				+ mkCloseFlag + ", mkOrderId=" + mkOrderId + ", number=" + number + ", price=" + price
				+ ", tkAccountId=" + tkAccountId + ", tkBidFlag=" + tkBidFlag + ", tkCloseFlag=" + tkCloseFlag
				+ ", tkOrderId=" + tkOrderId + ", tradeTime=" + tradeTime + ", asset=" + asset + ", symbol=" + symbol
				+ "]";
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
	
}
