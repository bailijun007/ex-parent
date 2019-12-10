/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.pc.module.order.entity;


import java.math.BigDecimal;
import java.util.Date;

import com.hp.sh.expv3.base.entity.UserDataEntity;

/**
 * 永续合约_订单（委托）
 */
public class PcOrder extends UserDataEntity{
	
	private static final long serialVersionUID = 1L;
	
	public static final int OPEN = 0 ;
	public static final int CLOSE = 1 ;

	public static final int PENDING_NEW = 1;
	public static final int NEW = 2;

	public static final int PENDING_CANCEL = 3;
	public static final int CANCELED = 4;
	
	//部分成交
	public static final int PARTIALLY_FILLED = 5;
	//全部成交
	public static final int FILLED = 6;
	
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
    private Integer closeFlag;
    /**
     * 是否：1-多仓，0-空仓
     */
    private Integer longFlag;
    /**
     * 杠杆
     */
    private BigDecimal leverage;
    /**
	 * 合约张数
	 */
	private BigDecimal volume;

	// 面值(单位：)
	private BigDecimal faceValue;

	/**
	 * 委托价格（单位：报价货币）
	 */
	private BigDecimal price;

    /**
     * 永续合约委托类型 @see PcOrderType#*
     */
    private Integer orderType;
    
	/**
	 * 委托有效时间 @see TimeInForce#*
	 */
	private Integer timeInForce;
	
	
	/**
	 * 保证金模式:1-全仓,2-逐仓
	 */
	private Integer marginMode;

	int ______系统设置_______;

    /**
	 * 开仓手续费率
	 */
	private BigDecimal openFeeRatio;

	/**
	 * 强平手续费率
	 */
	private BigDecimal closeFeeRatio;

	/**
	 * 保证金率，初始为 杠杆的倒数
	 */
	private BigDecimal marginRatio;
	
	/**
	 * 客户自定义委托ID，用于与客户系统关联 （open api）
	 */
	private String clientOrderId;

	int ______下单时计算_成交或撤单时可能会修改_______;
	
	/**
	 * 开仓手续费,成交时修改(可能部分成交，按比例收取)
	 */
	private BigDecimal openFee;

	/**
	 * 平仓手续费，在下委托时提前收取(可能部分成交，按比例收取)
	 */
	private BigDecimal closeFee;

	/**
	 * 委托保证金
	 */
	private BigDecimal orderMargin;

	/**
	 * @deprecated
	 * 总押金：委托保证金 + 开仓手续费 + 强平手续费 
	 */
	private BigDecimal grossMargin;

	/**
	 * 委托状态，{@link com.hupa.exp.base.enums.pc.PcOrderStatusEnum}
	 */
	private Integer status;

	// 是否活动委托
	private Integer activeFlag;

	private String remark;

	/**
	 * 取消时间
	 */
	private Date cancelTime;

	/**
	 * @deprecated
	 * 取消张数 (撤单时计算保存)：volume - filledVolume
	 */
	private BigDecimal cancelVolume;

	int ________系统下单后计算_成交或撤单时修改_________;
	
	/**
     * 已收手续费(可能部分成交，按比例收取)
     */
    private BigDecimal feeCost;
    /**
	 * 已成交量
	 */
	private BigDecimal filledVolume;
	/**
	 * 平仓委托对应的仓位Id
	 */
	private Long closePosId;
	/**
     * 是否已触发，用于止盈止损等触发式委托
     */
    private Integer triggerFlag;

    
    
//    int ________强平_________;

    /**
     * 可见性，强平委托，自动减仓委托 都不可见
     */
    private Integer visibleFlag;

//    int ________log__________;

    private String createOperator;
    
    private String cancelOperator;

    public PcOrder() {
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

	public Integer getCloseFlag() {
		return closeFlag;
	}

	public void setCloseFlag(Integer closeFlag) {
		this.closeFlag = closeFlag;
	}

	public Integer getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(Integer longFlag) {
		this.longFlag = longFlag;
	}

	public Integer getMarginMode() {
		return marginMode;
	}

	public void setMarginMode(Integer marginMode) {
		this.marginMode = marginMode;
	}

	public BigDecimal getLeverage() {
		return leverage;
	}

	public void setLeverage(BigDecimal leverage) {
		this.leverage = leverage;
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

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(BigDecimal faceValue) {
		this.faceValue = faceValue;
	}

	public BigDecimal getMarginRatio() {
		return marginRatio;
	}

	public void setMarginRatio(BigDecimal marginRatio) {
		this.marginRatio = marginRatio;
	}

	public BigDecimal getOpenFeeRatio() {
		return openFeeRatio;
	}

	public void setOpenFeeRatio(BigDecimal openFeeRatio) {
		this.openFeeRatio = openFeeRatio;
	}

	public BigDecimal getCloseFeeRatio() {
		return closeFeeRatio;
	}

	public void setCloseFeeRatio(BigDecimal closeFeeRatio) {
		this.closeFeeRatio = closeFeeRatio;
	}

	public BigDecimal getFeeCost() {
		return feeCost;
	}

	public void setFeeCost(BigDecimal feeCost) {
		this.feeCost = feeCost;
	}

	public BigDecimal getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(BigDecimal grossMargin) {
		this.grossMargin = grossMargin;
	}

	public BigDecimal getOrderMargin() {
		return orderMargin;
	}

	public void setOrderMargin(BigDecimal orderMargin) {
		this.orderMargin = orderMargin;
	}

	public BigDecimal getOpenFee() {
		return openFee;
	}

	public void setOpenFee(BigDecimal openFee) {
		this.openFee = openFee;
	}

	public BigDecimal getCloseFee() {
		return closeFee;
	}

	public void setCloseFee(BigDecimal closeFee) {
		this.closeFee = closeFee;
	}

	public BigDecimal getFilledVolume() {
		return filledVolume;
	}

	public void setFilledVolume(BigDecimal filledVolume) {
		this.filledVolume = filledVolume;
	}

	public Long getClosePosId() {
		return closePosId;
	}

	public void setClosePosId(Long closePosId) {
		this.closePosId = closePosId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTimeInForce() {
		return timeInForce;
	}

	public void setTimeInForce(Integer timeInForce) {
		this.timeInForce = timeInForce;
	}

	public Integer getTriggerFlag() {
		return triggerFlag;
	}

	public void setTriggerFlag(Integer triggerFlag) {
		this.triggerFlag = triggerFlag;
	}

	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}

	public Integer getVisibleFlag() {
		return visibleFlag;
	}

	public void setVisibleFlag(Integer visibleFlag) {
		this.visibleFlag = visibleFlag;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getClientOrderId() {
		return clientOrderId;
	}

	public void setClientOrderId(String clientOrderId) {
		this.clientOrderId = clientOrderId;
	}

	public BigDecimal getCancelVolume() {
		return cancelVolume;
	}

	public void setCancelVolume(BigDecimal cancelVolume) {
		this.cancelVolume = cancelVolume;
	}


}