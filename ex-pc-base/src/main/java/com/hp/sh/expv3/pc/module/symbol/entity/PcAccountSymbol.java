/**
 * @author zw
 * @date 2019/7/30
 */
package com.hp.sh.expv3.pc.module.symbol.entity;

import java.math.BigDecimal;

import javax.persistence.Version;

import com.hp.sh.expv3.base.entity.UserDataEntity;

/**
 * 永续合约_账户设置
 * @author lw
 *
 */
public class PcAccountSymbol extends UserDataEntity {

	private static final long serialVersionUID = 1L;

    // 资产
    private String asset;
    
    // 合约交易品种
    private String symbol;
    
    /**
	 * 保证金模式,
	 */
	private Integer marginMode;

	/**
     * 做空杠杆
     */
    private BigDecimal shortLeverage;
    
    /**
     * 做多杠杆
     */
    private BigDecimal longLeverage;
    
    /**
     * 最大多仓杠杆
     */
    private BigDecimal longMaxLeverage;
    
    /**
     * 最大多空杠杆
     */
    private BigDecimal shortMaxLeverage;

	/**
	 * 全仓杠杆
	 */
	private BigDecimal crossLeverage;

	//版本
	@Version
	private Long version;
	
	public PcAccountSymbol() {
	}

	public BigDecimal getShortMaxLeverage() {
		return shortMaxLeverage;
	}

	public void setShortMaxLeverage(BigDecimal shortMaxLeverage) {
		this.shortMaxLeverage = shortMaxLeverage;
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

	public BigDecimal getShortLeverage() {
		return shortLeverage;
	}

	public void setShortLeverage(BigDecimal shortLeverage) {
		this.shortLeverage = shortLeverage;
	}

	public BigDecimal getLongLeverage() {
		return longLeverage;
	}

	public void setLongLeverage(BigDecimal longLeverage) {
		this.longLeverage = longLeverage;
	}

	public Integer getMarginMode() {
		return marginMode;
	}

	public void setMarginMode(Integer marginMode) {
		this.marginMode = marginMode;
	}

	public BigDecimal getCrossLeverage() {
		return crossLeverage;
	}

	public void setCrossLeverage(BigDecimal crossLeverage) {
		this.crossLeverage = crossLeverage;
	}

	public BigDecimal getLongMaxLeverage() {
		return longMaxLeverage;
	}

	public void setLongMaxLeverage(BigDecimal maxLeverage) {
		this.longMaxLeverage = maxLeverage;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}