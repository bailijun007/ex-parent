package com.hp.sh.expv3.pc.module.order.entity;

import com.hp.sh.expv3.base.entity.UserData;

/**
 * 永续合约_活动订单（委托）
 */
public class PcActiveOrder implements UserData{
	
	//订单ID
	private Long id;
	
	//用户ID
	private Long userId;
	
    //资产
    private String asset;
    
    //合约交易品种
    private String symbol;
    
    //多空
    private Integer longFlag;

	public PcActiveOrder() {
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