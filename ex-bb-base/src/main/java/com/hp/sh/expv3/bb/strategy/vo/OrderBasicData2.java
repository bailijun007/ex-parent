package com.hp.sh.expv3.bb.strategy.vo;

import java.math.BigDecimal;

/**
 * 订单基本数据
 * @author wangjg
 *
 */
public class OrderBasicData2 {

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
	
	/////////////////////
	
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

	public OrderBasicData2() {
		super();
	}

	public Integer getCloseFlag() {
		return closeFlag;
	}

	public Integer getLongFlag() {
		return longFlag;
	}

	public BigDecimal getLeverage() {
		return leverage;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public BigDecimal getFaceValue() {
		return faceValue;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getOpenFeeRatio() {
		return openFeeRatio;
	}

	public BigDecimal getCloseFeeRatio() {
		return closeFeeRatio;
	}

	public BigDecimal getMarginRatio() {
		return marginRatio;
	}
	
	
}
