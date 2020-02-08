package com.hp.sh.expv3.bb.module.order.entity;

import javax.persistence.Table;

import com.hp.sh.expv3.base.entity.UserData;

/**
 * 币币_活动订单（委托）
 */
@Table(name="bb_active_order")
public class BBActiveOrder implements UserData{
	
	//订单ID
	private Long id;
	
	//用户ID
	private Long userId;
	
    //资产
    private String asset;
    
    //合约交易品种
    private String symbol;
    
    //买卖
    private Integer bidFlag;

	public BBActiveOrder() {
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

	public Integer getBidFlag() {
		return bidFlag;
	}

	public void setBidFlag(Integer longFlag) {
		this.bidFlag = longFlag;
	}

}