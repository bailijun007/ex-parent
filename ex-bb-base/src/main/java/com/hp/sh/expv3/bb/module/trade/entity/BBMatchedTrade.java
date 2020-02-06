/**
 * @author zw
 * @date 2019/8/23
 */
package com.hp.sh.expv3.bb.module.trade.entity;

import java.math.BigDecimal;

import javax.persistence.Table;

/**
 * 撮合结果
 *
 */
@Table(name="bb_trade")
public class BBMatchedTrade {

	//id
	private Long id;
	//事务Id
    private Long matchTxId;
    //交易对
    private String symbol;
    //资产
    private String asset;

    //taker是否买：1-是，0-否
    private Integer tkBidFlag;

    /*** taker ***/
    //taker账户ID
    private Long tkAccountId;
    //taker订单ID
    private Long tkOrderId;
    //taker是否平仓
    private Integer tkCloseFlag;

    /*** maker ***/
    //maker账户Id
    private Long mkAccountId;
    //maker订单ID
    private Long mkOrderId;
    //maker是否平仓
    private Integer mkCloseFlag;

    /*** data ***/
    //成交价格
    private BigDecimal price;
    //数量
    private BigDecimal number;
    //成交时间
    private Long tradeTime;
    
    ///////////////////////

    private Integer makerHandleStatus;
    
    private Integer takerHandleStatus;
    
	public BBMatchedTrade() {
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

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public Integer getTkBidFlag() {
		return tkBidFlag;
	}

	public void setTkBidFlag(Integer tkBidFlag) {
		this.tkBidFlag = tkBidFlag;
	}

	public Long getTkAccountId() {
		return tkAccountId;
	}

	public void setTkAccountId(Long tkAccountId) {
		this.tkAccountId = tkAccountId;
	}

	public Long getTkOrderId() {
		return tkOrderId;
	}

	public void setTkOrderId(Long tkOrderId) {
		this.tkOrderId = tkOrderId;
	}

	public Integer getTkCloseFlag() {
		return tkCloseFlag;
	}

	public void setTkCloseFlag(Integer tkCloseFlag) {
		this.tkCloseFlag = tkCloseFlag;
	}

	public Long getMkAccountId() {
		return mkAccountId;
	}

	public void setMkAccountId(Long mkAccountId) {
		this.mkAccountId = mkAccountId;
	}

	public Long getMkOrderId() {
		return mkOrderId;
	}

	public void setMkOrderId(Long mkOrderId) {
		this.mkOrderId = mkOrderId;
	}

	public Integer getMkCloseFlag() {
		return mkCloseFlag;
	}

	public void setMkCloseFlag(Integer mkCloseFlag) {
		this.mkCloseFlag = mkCloseFlag;
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

	public Long getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Long tradeTime) {
		this.tradeTime = tradeTime;
	}

	public Integer getMakerHandleStatus() {
		return makerHandleStatus;
	}

	public void setMakerHandleStatus(Integer makerHandleStatus) {
		this.makerHandleStatus = makerHandleStatus;
	}

	public Integer getTakerHandleStatus() {
		return takerHandleStatus;
	}

	public void setTakerHandleStatus(Integer takerHandleStatus) {
		this.takerHandleStatus = takerHandleStatus;
	}
}
