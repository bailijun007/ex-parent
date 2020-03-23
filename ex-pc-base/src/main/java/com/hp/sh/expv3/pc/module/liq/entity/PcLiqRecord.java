package com.hp.sh.expv3.pc.module.liq.entity;

import java.math.BigDecimal;

import javax.persistence.Version;

import com.hp.sh.expv3.base.entity.UserDataEntity;

/**
 * 永续合约_仓位
 */
public class PcLiqRecord extends UserDataEntity {

	private static final long serialVersionUID = 1L;

    // 资产
	private String asset;

	// 合约交易品种
    private String symbol;

	//仓位ID
	private Long posId;
	
    // 是否多仓(side)
	private Integer longFlag;

	//数量（张）
	private BigDecimal volume;
	
	//保证金
	private BigDecimal posMargin;
	
	//破产价
	private BigDecimal bankruptPrice;
	
	//强平价
	private BigDecimal liqPrice;
	
	//仓位均价
	private BigDecimal meanPrice;
	
	//手续费
	private BigDecimal fee;
	
	//手续费率
	private BigDecimal feeRatio;
	
	// 强平委托已成交量
	private BigDecimal filledVolume;
	
	// 强平收益
	private BigDecimal pnl;
	
	private Integer status;
	
	//版本
	private Long version;

	public PcLiqRecord() {
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

	public Long getPosId() {
		return posId;
	}

	public void setPosId(Long posId) {
		this.posId = posId;
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

	public BigDecimal getBankruptPrice() {
		return bankruptPrice;
	}

	public void setBankruptPrice(BigDecimal bankruptPrice) {
		this.bankruptPrice = bankruptPrice;
	}

	public BigDecimal getPosMargin() {
		return posMargin;
	}

	public void setPosMargin(BigDecimal posMargin) {
		this.posMargin = posMargin;
	}

	public BigDecimal getFilledVolume() {
		return filledVolume;
	}

	public void setFilledVolume(BigDecimal filledVolume) {
		this.filledVolume = filledVolume;
	}

	public BigDecimal getPnl() {
		return pnl;
	}

	public void setPnl(BigDecimal pnl) {
		this.pnl = pnl;
	}

	public BigDecimal getLiqPrice() {
		return liqPrice;
	}

	public void setLiqPrice(BigDecimal liqPrice) {
		this.liqPrice = liqPrice;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getFeeRatio() {
		return feeRatio;
	}

	public void setFeeRatio(BigDecimal feeRatio) {
		this.feeRatio = feeRatio;
	}

	public BigDecimal getMeanPrice() {
		return meanPrice;
	}

	public void setMeanPrice(BigDecimal meanPrice) {
		this.meanPrice = meanPrice;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Version
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
