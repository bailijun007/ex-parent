package com.hp.sh.expv3.pc.module.position.vo;

import com.hp.sh.expv3.base.entity.UserDataEntityUID;

/**
 * 仓位唯一标识
 * @author wangjg
 *
 */
public class PosUID extends UserDataEntityUID{
    // 资产
	private String asset;

	// 合约交易品种
    private String symbol;
    
    // 是否多仓(side)
	private Integer longFlag;

	public PosUID() {
	}

	public PosUID(Long id, Long userId, String asset, String symbol, Integer longFlag) {
		this.id=id;
		this.userId = userId;
		this.asset = asset;
		this.symbol = symbol;
		this.longFlag = longFlag;
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

	@Override
	public String toString() {
		return "PosUID [id=" + id + ", userId=" + userId + ", asset=" + asset + ", symbol=" + symbol + ", longFlag="
				+ longFlag + "]";
	}

}
