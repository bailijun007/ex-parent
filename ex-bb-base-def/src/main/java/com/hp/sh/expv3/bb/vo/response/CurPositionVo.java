package com.hp.sh.expv3.bb.vo.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

/**
 * 修改保证金
 * @author wangjg
 *
 */
public class CurPositionVo {

	@ApiModelProperty("资产")
	private String asset;
	
	@ApiModelProperty("品种")
	private String symbol;
	
	@ApiModelProperty("多/空")
	private Integer longFlag;
	
	@ApiModelProperty("持仓(张)")
	private BigDecimal volume;

	@ApiModelProperty("可平量")
	private BigDecimal closableVolume;

	@ApiModelProperty("保证金")
	private BigDecimal posMargin;

	@ApiModelProperty("当前杠杆")
	private BigDecimal leverage;
	
	@ApiModelProperty("开仓均价")
	private BigDecimal meanPrice;

	int _______transient_________;

	@ApiModelProperty("保证金率")
	private BigDecimal posMarginRatio;

	@ApiModelProperty("收益率")
	private BigDecimal pnlRatio;

	/****************** 强平 ****************/
	
	@ApiModelProperty("预估强评价")
	private BigDecimal liqPrice;

	@ApiModelProperty("已实现盈亏")
	private BigDecimal realisedPnl;
	
	@ApiModelProperty("未实现盈亏")
	private BigDecimal floatingPnl;

	@ApiModelProperty("维持保证金率")
	private BigDecimal holdMarginRatio;

	int _______unused_________;

	// 是否自动追加保证金标识
	private Integer autoAddFlag;

	// 面值(单位：报价货币)
	private BigDecimal faceValue;

	/*
	 * 仓位 基础货币 总价值（含义不明，不是 用当前价格计算的）（按均价计算的仓位价值）
	 * posBaseValue  
	 */
	private BigDecimal baseValue;
	
	int ________________________________________;

	public CurPositionVo() {
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

	public Integer getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(Integer longFlag) {
		this.longFlag = longFlag;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getClosableVolume() {
		return closableVolume;
	}

	public void setClosableVolume(BigDecimal closableVolume) {
		this.closableVolume = closableVolume;
	}

	public BigDecimal getPosMargin() {
		return posMargin;
	}

	public void setPosMargin(BigDecimal posMargin) {
		this.posMargin = posMargin;
	}

	public BigDecimal getLeverage() {
		return leverage;
	}

	public void setLeverage(BigDecimal leverage) {
		this.leverage = leverage;
	}

	public BigDecimal getMeanPrice() {
		return meanPrice;
	}

	public void setMeanPrice(BigDecimal meanPrice) {
		this.meanPrice = meanPrice;
	}

	public BigDecimal getPosMarginRatio() {
		return posMarginRatio;
	}

	public void setPosMarginRatio(BigDecimal posMarginRatio) {
		this.posMarginRatio = posMarginRatio;
	}

	public BigDecimal getPnlRatio() {
		return pnlRatio;
	}

	public void setPnlRatio(BigDecimal pnlRatio) {
		this.pnlRatio = pnlRatio;
	}

	public BigDecimal getLiqPrice() {
		return liqPrice;
	}

	public void setLiqPrice(BigDecimal liqPrice) {
		this.liqPrice = liqPrice;
	}

	public BigDecimal getRealisedPnl() {
		return realisedPnl;
	}

	public void setRealisedPnl(BigDecimal realisedPnl) {
		this.realisedPnl = realisedPnl;
	}

	public BigDecimal getFloatingPnl() {
		return floatingPnl;
	}

	public void setFloatingPnl(BigDecimal floatingPnl) {
		this.floatingPnl = floatingPnl;
	}

	public BigDecimal getHoldMarginRatio() {
		return holdMarginRatio;
	}

	public void setHoldMarginRatio(BigDecimal holdMarginRatio) {
		this.holdMarginRatio = holdMarginRatio;
	}

	public Integer getAutoAddFlag() {
		return autoAddFlag;
	}

	public void setAutoAddFlag(Integer autoAddFlag) {
		this.autoAddFlag = autoAddFlag;
	}

	public BigDecimal getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(BigDecimal faceValue) {
		this.faceValue = faceValue;
	}

	public BigDecimal getBaseValue() {
		return baseValue;
	}

	public void setBaseValue(BigDecimal baseValue) {
		this.baseValue = baseValue;
	}

	@Override
	public String toString() {
		return "CurPositionVo [asset=" + asset + ", symbol=" + symbol + ", longFlag=" + longFlag + ", volume=" + volume
				+ ", closableVolume=" + closableVolume + ", posMargin=" + posMargin + ", leverage=" + leverage
				+ ", meanPrice=" + meanPrice + ", posMarginRatio=" + posMarginRatio + ", pnlRatio=" + pnlRatio
				+ ", liqPrice=" + liqPrice + ", realisedPnl=" + realisedPnl + ", floatingPnl=" + floatingPnl
				+ ", holdMarginRatio=" + holdMarginRatio + ", autoAddFlag=" + autoAddFlag + ", faceValue=" + faceValue
				+ ", baseValue=" + baseValue + "]";
	}


}
