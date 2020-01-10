/**
 * @author 10086
 * @date 2019/10/30
 */
package com.hp.sh.expv3.pc.module.order.entity;

import java.math.BigDecimal;

import javax.persistence.Transient;

import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.commons.mybatis.TxId;
import com.hp.sh.expv3.dev.Question;

/**
 * 永续合约_用户订单成交记录
 * 
 */
public class PcOrderTrade extends UserDataEntity {

	private static final long serialVersionUID = 1L;

	//资产
	private String asset;

	//合约交易品种
	private String symbol;

	//成交价
	private BigDecimal price;

	//成交量（张数）
	private BigDecimal volume;
	
	//交易序号
	private String tradeSn;
	
	//交易ID
	private Long tradeId;

	//仓位ID
	private Long posId;

	//订单ID
	private Long orderId;

	// 1-marker， 0-taker
	private Integer makerFlag;

	//成交时间
	private Long tradeTime;
	
//	int ______________;

	//手续费收取人
	private Long feeCollectorId;
	
	//手续费率
	private BigDecimal feeRatio;
	
	//手续费
	private BigDecimal fee;
	
	//盈亏(此次成交的盈亏)
	@Question(ask="没有用到，老项目里也没这个字段", answer="老杜用")
	private BigDecimal pnl;
	
	//未成交（张数）
	private BigDecimal remainVolume;
	
	//撮合事务Id
	private Long matchTxId;
	
	//事务ID
	private Long txId;
	
	@Transient
	private Integer tradType;

	public PcOrderTrade() {
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

	public BigDecimal getPnl() {
		return pnl;
	}

	public void setPnl(BigDecimal pnl) {
		this.pnl = pnl;
	}

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public Long getPosId() {
		return posId;
	}

	public void setPosId(Long posId) {
		this.posId = posId;
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

	@Override
	public String toString() {
		return "PcOrderTrade [asset=" + asset + ", symbol=" + symbol + ", price=" + price + ", volume=" + volume
				+ ", tradeSn=" + tradeSn + ", tradeId=" + tradeId + ", posId=" + posId + ", orderId=" + orderId
				+ ", makerFlag=" + makerFlag + ", tradeTime=" + tradeTime + ", feeCollectorId=" + feeCollectorId
				+ ", feeRatio=" + feeRatio + ", fee=" + fee + ", pnl=" + pnl + ", remainVolume=" + remainVolume
				+ ", matchTxId=" + matchTxId + ", txId=" + txId + ", tradType=" + tradType + "]";
	}

	public Integer getTradType() {
		return tradType;
	}

	public void setTradType(Integer tradType) {
		this.tradType = tradType;
	}

}
