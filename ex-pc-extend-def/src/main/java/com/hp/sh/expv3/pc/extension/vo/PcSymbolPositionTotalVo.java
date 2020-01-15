package com.hp.sh.expv3.pc.extension.vo;

import java.math.BigDecimal;

/**
 * 合约仓位统计
 * @author wangjg
 *
 */
public class PcSymbolPositionTotalVo {
	
	private String asset;
	
	private String symbol;

	//多仓
	private BigDecimal longVolume;

	//空仓
	private BigDecimal shortVolume;

	//多仓数量
	private BigDecimal longPosNum;

	//多仓数量
	private BigDecimal shortPosNum;

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

	public BigDecimal getLongVolume() {
		return longVolume;
	}

	public void setLongVolume(BigDecimal longVolume) {
		this.longVolume = longVolume;
	}

	public BigDecimal getShortVolume() {
		return shortVolume;
	}

	public void setShortVolume(BigDecimal shortVolume) {
		this.shortVolume = shortVolume;
	}

	public BigDecimal getLongPosNum() {
		return longPosNum;
	}

	public void setLongPosNum(BigDecimal longPosNum) {
		this.longPosNum = longPosNum;
	}

	public BigDecimal getShortPosNum() {
		return shortPosNum;
	}

	public void setShortPosNum(BigDecimal shortPosNum) {
		this.shortPosNum = shortPosNum;
	}

	
}
