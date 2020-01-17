package com.hp.sh.expv3.pc.module.position.entity;

import java.math.BigDecimal;

import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.dev.Redundant;

/**
 * 永续合约_仓位
 * @author wangjg
 *
 */
public class PcPosition extends UserDataEntity {

	private static final long serialVersionUID = 1L;

    // 资产
	private String asset;

	// 合约交易品种
    private String symbol;
    
    // 是否多仓(side)
	private Integer longFlag;

	// 保证金模式:1-全仓,2-逐仓
	private int marginMode;

	// 开仓杠杆 @see PcOrder#leverage
	private BigDecimal entryLeverage;

	int ________________;

	// 是否自动追加保证金标识
	private Integer autoAddFlag;

	// 当前杠杆
	private BigDecimal leverage;

	// 面值(单位：报价货币)
	private BigDecimal faceValue;

	/*
	 * 仓位 张数 
	 * posVolume
	 */
	private BigDecimal volume;
	
	/*
	 * 仓位 基础货币 总价值（含义不明，不是 用当前价格计算的）（按均价计算的仓位价值）
	 * posBaseValue  
	 */
	private BigDecimal baseValue;

	/**
	 * 仓位保证金，  随开仓平仓加减 ;追加保证金也加
	 */
	private BigDecimal posMargin;
	
	//平仓手续费
	private BigDecimal closeFee;

	/**
     * 均价，仓位为0时，表示最后一次仓位变动时的均价
     * meanPrice
     */
    private BigDecimal meanPrice;
    
    //累积总价值
    private BigDecimal accuBaseValue;
    
    //累计成交量
    private BigDecimal accuVolume;
    
    /**
	 * 初始保证金，随开仓平仓加减；追加保证金不变
	 */
	private BigDecimal initMargin;
    
  int _____________________________________________________;

	/**
     * 维持保证金率
     */
	@Redundant
    private BigDecimal holdMarginRatio;
    
    // 已扣手续费
    private BigDecimal feeCost;
    
    // 已实现盈亏
    private BigDecimal realisedPnl;
	
	/****************** 强平 ****************/
    
    //预估强评价
    private BigDecimal liqPrice;

	// 仓位强平状态，0：未触发平仓，1：仓位被冻结，
	private Integer liqStatus;

	// 触发强平的标记价格
	private BigDecimal liqMarkPrice;

	// 触发强平的标记时间
	private Long liqMarkTime;

	
	
	//仓位已平空
//	private Integer closed;

	public PcPosition() {
		super();
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public BigDecimal getLiqMarkPrice() {
		return liqMarkPrice;
	}

	public void setLiqMarkPrice(BigDecimal liqMarkPrice) {
		this.liqMarkPrice = liqMarkPrice;
	}

	public Long getLiqMarkTime() {
		return liqMarkTime;
	}

	public void setLiqMarkTime(Long liqMarkTime) {
		this.liqMarkTime = liqMarkTime;
	}

	public Integer getLiqStatus() {
		return liqStatus;
	}

	public void setLiqStatus(Integer liqStatus) {
		this.liqStatus = liqStatus;
	}

	public Integer getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(Integer longFlag) {
		this.longFlag = longFlag;
	}

	public int getMarginMode() {
		return marginMode;
	}

	public void setMarginMode(int marginMode) {
		this.marginMode = marginMode;
	}

	public BigDecimal getBaseValue() {
		return baseValue;
	}

	public void setBaseValue(BigDecimal baseValue) {
		this.baseValue = baseValue;
	}

	public BigDecimal getLeverage() {
		return leverage;
	}

	public void setLeverage(BigDecimal leverage) {
		this.leverage = leverage;
	}

	public BigDecimal getEntryLeverage() {
		return entryLeverage;
	}

	public void setEntryLeverage(BigDecimal entryLeverage) {
		this.entryLeverage = entryLeverage;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getMeanPrice() {
		return meanPrice;
	}

	public void setMeanPrice(BigDecimal averagePrice) {
		this.meanPrice = averagePrice;
	}

	public BigDecimal getPosMargin() {
		return posMargin;
	}

	public void setPosMargin(BigDecimal posMargin) {
		this.posMargin = posMargin;
	}

	public BigDecimal getHoldMarginRatio() {
		return holdMarginRatio;
	}

	public void setHoldMarginRatio(BigDecimal holdRatio) {
		this.holdMarginRatio = holdRatio;
	}

	public BigDecimal getInitMargin() {
		return initMargin;
	}

	public void setInitMargin(BigDecimal initMargin) {
		this.initMargin = initMargin;
	}

	public BigDecimal getRealisedPnl() {
		return realisedPnl;
	}

	public void setRealisedPnl(BigDecimal realisedPnl) {
		this.realisedPnl = realisedPnl;
	}

	public Integer getAutoAddFlag() {
		return autoAddFlag;
	}

	public void setAutoAddFlag(Integer autoAddFlag) {
		this.autoAddFlag = autoAddFlag;
	}

	public BigDecimal getFeeCost() {
		return feeCost;
	}

	public void setFeeCost(BigDecimal feeCost) {
		this.feeCost = feeCost;
	}

	public BigDecimal getCloseFee() {
		return closeFee;
	}

	public void setCloseFee(BigDecimal closeFee) {
		this.closeFee = closeFee;
	}

	public BigDecimal getAccuBaseValue() {
		return accuBaseValue;
	}

	public void setAccuBaseValue(BigDecimal accuBaseValue) {
		this.accuBaseValue = accuBaseValue;
	}

	public BigDecimal getAccuVolume() {
		return accuVolume;
	}

	public void setAccuVolume(BigDecimal accuVolume) {
		this.accuVolume = accuVolume;
	}

	public BigDecimal getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(BigDecimal faceValue) {
		this.faceValue = faceValue;
	}

	public BigDecimal getLiqPrice() {
		return liqPrice;
	}

	public void setLiqPrice(BigDecimal liqPrice) {
		this.liqPrice = liqPrice;
	}

}
