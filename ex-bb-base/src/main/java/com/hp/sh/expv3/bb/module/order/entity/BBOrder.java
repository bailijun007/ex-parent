package com.hp.sh.expv3.bb.module.order.entity;


import java.math.BigDecimal;

import javax.persistence.Table;

import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.bb.strategy.data.OrderData;

/**
 * 币币_订单（委托）
 */
@Table(name="bb_order")
public class BBOrder extends UserDataEntity implements OrderData{
	
	private static final long serialVersionUID = 1L;
	
//	int _______记录_______;

    /**
     * 
     * 资产
     */
    private String asset;
    /**
     * 合约交易品种
     */
    private String symbol;
    /**
     * 是否:1-平仓,0-开
     */
    private Integer bidFlag;
    /**
     * 杠杆
     */
    private BigDecimal leverage;
    /**
	 * 合约张数
	 */
	private BigDecimal volume;

	/**
	 * 委托价格（单位：报价货币）
	 */
	private BigDecimal price;

    /**
     * 币币委托类型 @see BBOrderType#*
     */
    private Integer orderType;
    
	/**
	 * 委托有效时间 @see TimeInForce#*
	 */
	private Integer timeInForce;
	
//	int ______系统设置_______;

    /**
	 * 开仓手续费率
	 */
	private BigDecimal feeRatio;

	/**
	 * 客户自定义委托ID，用于与客户系统关联 （open api）
	 */
	private String clientOrderId;

//	int ______下单时计算_成交或撤单时可能会修改_______;
	
	/**
	 * 开仓手续费,成交时修改(可能部分成交，按比例释放)
	 */
	private BigDecimal fee;

	/**
	 * 委托保证金
	 */
	private BigDecimal orderMargin;

	//保证金货币类型
	private String orderMarginCurrency;

	/**
	 * 委托状态，OrderStatus#*
	 */
	private Integer status;

	// 是否活动委托
	@Deprecated
	private Integer activeFlag;

	private String remark;

	/**
	 * 取消时间
	 */
	private Long cancelTime;

	/**
	 * @deprecated
	 * 取消张数 (撤单时计算保存)：volume - filledVolume
	 */
	private BigDecimal cancelVolume;

//	int ________系统下单后计算_成交或撤单时修改_________;
	
	/**
     * 已收手续费(可能部分成交，按比例收取)
     */
    private BigDecimal feeCost;
    /**
	 * 已成交量
	 */
	private BigDecimal filledVolume;

	//版本
	private Long version;

//    int ________log__________;

    private String createOperator;
    
    private String cancelOperator;

    public BBOrder() {
	}
	
	public BigDecimal getGrossMargin() {
		return this.orderMargin.add(this.fee);
	}
	
//	int ____________________________;
	
	@Override
	public BigDecimal getFaceValue() {
		return BigDecimal.ONE;
	}

	@Override
	public BigDecimal getMarginRatio() {
		return BigDecimal.ONE;
	}

	@Override
	public BigDecimal getCloseFeeRatio() {
		return BigDecimal.ZERO;
	}
	
//	int _____________________________;

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

	public void setBidFlag(Integer bidFlag) {
		this.bidFlag = bidFlag;
	}

	public BigDecimal getLeverage() {
		return leverage;
	}

	public void setLeverage(BigDecimal leverage) {
		this.leverage = leverage;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getTimeInForce() {
		return timeInForce;
	}

	public void setTimeInForce(Integer timeInForce) {
		this.timeInForce = timeInForce;
	}

	public BigDecimal getFeeRatio() {
		return feeRatio;
	}

	public void setFeeRatio(BigDecimal feeRatio) {
		this.feeRatio = feeRatio;
	}

	public String getClientOrderId() {
		return clientOrderId;
	}

	public void setClientOrderId(String clientOrderId) {
		this.clientOrderId = clientOrderId;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getOrderMargin() {
		return orderMargin;
	}

	public void setOrderMargin(BigDecimal orderMargin) {
		this.orderMargin = orderMargin;
	}

	public String getOrderMarginCurrency() {
		return orderMarginCurrency;
	}

	public void setOrderMarginCurrency(String orderMarginCurrency) {
		this.orderMarginCurrency = orderMarginCurrency;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Long cancelTime) {
		this.cancelTime = cancelTime;
	}

	public BigDecimal getCancelVolume() {
		return cancelVolume;
	}

	public void setCancelVolume(BigDecimal cancelVolume) {
		this.cancelVolume = cancelVolume;
	}

	public BigDecimal getFeeCost() {
		return feeCost;
	}

	public void setFeeCost(BigDecimal feeCost) {
		this.feeCost = feeCost;
	}

	public BigDecimal getFilledVolume() {
		return filledVolume;
	}

	public void setFilledVolume(BigDecimal filledVolume) {
		this.filledVolume = filledVolume;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(String createOperator) {
		this.createOperator = createOperator;
	}

	public String getCancelOperator() {
		return cancelOperator;
	}

	public void setCancelOperator(String cancelOperator) {
		this.cancelOperator = cancelOperator;
	}
    
}