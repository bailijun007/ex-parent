package com.hp.sh.expv3.bb.module.order.entity;

import java.math.BigDecimal;

import javax.persistence.Table;

import com.hp.sh.expv3.base.entity.UserData;
import com.hp.sh.expv3.bb.strategy.data.OrderTrade;
import com.hp.sh.expv3.commons.mybatis.TxId;
import com.hp.sh.expv3.utils.math.BigUtils;

/**
 * 币币_用户订单成交记录
 * 
 */
@Table(name="bb_order_trade")
public class BBOrderTrade implements OrderTrade, UserData {

	//资产
	private String asset;

	//合约交易品种
	private String symbol;

	//成交价
	private BigDecimal price;

	//成交量（个数）
	private BigDecimal volume;
	
	private Integer bidFlag;
	
	//交易序号
	private String tradeSn;
	
	//交易ID
	private Long tradeId;

	//订单ID
	private Long orderId;

	// 1-marker， 0-taker
	private Integer makerFlag;

	//成交时间
	private Long tradeTime;
	
//	int ______________;
	
	//对手订单ID
	private Long opponentOrderId;

	//手续费收取人
	private Long feeCollectorId;
	
	//手续费率
	private BigDecimal feeRatio;
	
	//手续费
	private BigDecimal fee;
	
	//押金
	private BigDecimal orderMargin;
	
	//未成交数量
	private BigDecimal remainVolume;
	//订单剩余保证金
	private BigDecimal remainOrderMargin;
	//订单剩余手续费
	private BigDecimal remainFee;
	
	//撮合事务Id
	private Long matchTxId;

	//事务ID
	private Long txId;
	
	//手续费同步状态
	private Integer feeSynchStatus;
	
	//用户ID
	private Long userId;
	
	private Long id;

	// 创建时间
	private Long created;
	// 修改时间
	private Long modified;
	
	public BBOrderTrade() {
		super();
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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public String getTradeSn() {
		return tradeSn;
	}

	public void setTradeSn(String tradeId) {
		this.tradeSn = tradeId;
	}

	public Integer getMakerFlag() {
		return makerFlag;
	}

	public void setMakerFlag(Integer makerFlag) {
		this.makerFlag = makerFlag;
	}

	public Long getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Long tradeTime) {
		this.tradeTime = tradeTime;
	}

	public Long getFeeCollectorId() {
		return feeCollectorId;
	}

	public void setFeeCollectorId(Long feeCollectorId) {
		this.feeCollectorId = feeCollectorId;
	}

	public BigDecimal getFeeRatio() {
		return feeRatio;
	}

	public void setFeeRatio(BigDecimal feeRatio) {
		this.feeRatio = feeRatio;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public BigDecimal getRemainVolume() {
		return remainVolume;
	}

	public void setRemainVolume(BigDecimal remainVolume) {
		this.remainVolume = remainVolume;
	}

	public Long getMatchTxId() {
		return matchTxId;
	}

	public void setMatchTxId(Long matchTxId) {
		this.matchTxId = matchTxId;
	}
	
	@TxId
	public Long getTxId() {
		return txId;
	}

	public void setTxId(Long txId) {
		this.txId = txId;
	}

	public Integer getBidFlag() {
		return bidFlag;
	}

	public void setBidFlag(Integer bidFlag) {
		this.bidFlag = bidFlag;
	}

	public BigDecimal getRemainOrderMargin() {
		return remainOrderMargin;
	}

	public void setRemainOrderMargin(BigDecimal remainOrderMargin) {
		this.remainOrderMargin = remainOrderMargin;
	}

	public BigDecimal getRemainFee() {
		return remainFee;
	}

	public void setRemainFee(BigDecimal remainFee) {
		this.remainFee = remainFee;
	}

	public Integer getFeeSynchStatus() {
		return feeSynchStatus;
	}

	public void setFeeSynchStatus(Integer feeSynchStatus) {
		this.feeSynchStatus = feeSynchStatus;
	}

	public BigDecimal getOrderMargin() {
		return orderMargin;
	}

	public void setOrderMargin(BigDecimal orderMargin) {
		this.orderMargin = orderMargin;
	}

	public Long getOpponentOrderId() {
		return opponentOrderId;
	}

	public void setOpponentOrderId(Long opponentOrderId) {
		this.opponentOrderId = opponentOrderId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getModified() {
		return modified;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}

	@Override
	public String toString() {
		return "BBOrderTrade [asset=" + asset + ", symbol=" + symbol + ", price=" + price + ", volume=" + volume
				+ ", bidFlag=" + bidFlag + ", tradeSn=" + tradeSn + ", tradeId=" + tradeId + ", orderId=" + orderId
				+ ", makerFlag=" + makerFlag + ", tradeTime=" + tradeTime + ", feeCollectorId=" + feeCollectorId
				+ ", feeRatio=" + feeRatio + ", fee=" + fee + ", remainVolume=" + remainVolume + ", remainOrderMargin="
				+ remainOrderMargin + ", remainFee=" + remainFee + ", matchTxId=" + matchTxId + ", txId=" + txId
				+ ", feeSynchStatus=" + feeSynchStatus + "]";
	}

	public boolean isOrderCompleted() {
		return BigUtils.isZero(this.remainVolume);
	}

}
