package com.hp.sh.expv3.bb.vo.response;

import java.math.BigDecimal;

/**
 * 活动委托
 * @author wangjg
 *
 */
public class ActiveOrderVo {

	//订单ID
	private Long id;
	
	//用户ID
	private Long userId;
	
    //资产
    private String asset;
    
    //合约交易品种
    private String symbol;
    
	// 创建时间
	private Long created;
	
    //是否:1-平仓,0-开
    private Integer bidFlag;
	
    //杠杆
    private BigDecimal leverage;

    //成交量
    private BigDecimal filledVolume;
	
    //成交比例
    private BigDecimal filledRatio;
	
    //成交均价
    private BigDecimal meanPrice;
	
	//委托价格
	private BigDecimal price;
	
	//已收手续费
    private BigDecimal feeCost;
	
	//委托状态，OrderStatus#*
	private Integer status;

	public ActiveOrderVo() {
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

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Integer getBidFlag() {
		return bidFlag;
	}

	public void setBidFlag(Integer closeFlag) {
		this.bidFlag = closeFlag;
	}

	public BigDecimal getLeverage() {
		return leverage;
	}

	public void setLeverage(BigDecimal leverage) {
		this.leverage = leverage;
	}

	public BigDecimal getFilledVolume() {
		return filledVolume;
	}

	public void setFilledVolume(BigDecimal filledVolume) {
		this.filledVolume = filledVolume;
	}

	public BigDecimal getFilledRatio() {
		return filledRatio;
	}

	public void setFilledRatio(BigDecimal filledRatio) {
		this.filledRatio = filledRatio;
	}

	public BigDecimal getMeanPrice() {
		return meanPrice;
	}

	public void setMeanPrice(BigDecimal meanPrice) {
		this.meanPrice = meanPrice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getFeeCost() {
		return feeCost;
	}

	public void setFeeCost(BigDecimal feeCost) {
		this.feeCost = feeCost;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
