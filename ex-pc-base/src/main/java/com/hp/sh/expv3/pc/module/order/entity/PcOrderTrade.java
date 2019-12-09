/**
 * @author 10086
 * @date 2019/10/30
 */
package com.hp.sh.expv3.pc.module.order.entity;

import java.math.BigDecimal;

import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.pc.atemp.Question;

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

	//订单ID
	private Long orderId;
	
	//成交价
	private BigDecimal price;

	//成交量（单位：【结算货币】）
	private BigDecimal volume;

	//交易ID
	private Long tradeId;

	// 1-marker， 0-taker
	private Integer makerFlag;

	//成交时间
	private Long tradeTime;
	
	int ______________;

	//手续费收取人
	private Long feeCollectorId;
	
	//手续费率
	private BigDecimal feeRatio;
	
	//手续费
	private BigDecimal fee;
	
	//盈亏(此次成交的盈亏)
	@Question("没有用到，老项目里也没这个字段")
	private BigDecimal pnl;

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

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
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

	@Override
	public String toString() {
		return "PcOrderTrade [asset=" + asset + ", symbol=" + symbol + ", orderId=" + orderId + ", price=" + price
				+ ", volume=" + volume + ", tradeId=" + tradeId + ", makerFlag=" + makerFlag + ", tradeTime="
				+ tradeTime + ", feeCollectorId=" + feeCollectorId + ", feeRatio=" + feeRatio + ", fee=" + fee
				+ ", pnl=" + pnl + "]";
	}

}
