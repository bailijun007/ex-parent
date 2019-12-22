/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.pc.module.position.entity;

import java.math.BigDecimal;

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

}
