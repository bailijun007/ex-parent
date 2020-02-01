package com.hp.sh.expv3.pc.module.position.entity;

import com.hp.sh.expv3.base.entity.UserData;

/**
 * 永续合约_活动仓位
 */
public class PcActivePosition implements UserData{
	
	//仓位ID
	private Long id;
	
	//用户ID
	private Long userId;
	
    //资产
    private String asset;
    
    //合约交易品种
    private String symbol;
    
    //多空
    private Integer longFlag;

	public PcActivePosition() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

}