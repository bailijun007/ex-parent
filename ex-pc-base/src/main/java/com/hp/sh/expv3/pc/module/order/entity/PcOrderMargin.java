/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.pc.module.order.entity;


import java.math.BigDecimal;

import com.hp.sh.expv3.base.entity.UserDataEntity;

/**
 * 永续合约_订单（委托）
 */
public class PcOrderMargin extends UserDataEntity {
	
    /**
	 * 保证金率，初始为 杠杆的倒数
	 */
	private BigDecimal marginRatio;
	/**
	 * 开仓手续费率
	 */
	private BigDecimal openFeeRatio;
	/**
	 * 强平手续费率
	 */
	private BigDecimal closeFeeRatio;

	/**
     * 实收手续费,成交后计算(可能部分成交，按比例收取)
     */
    private BigDecimal feeCost;
    /**
     * @deprecated
     * 总押金：委托保证金 + 开仓手续费 + 强平手续费 
     */
    private BigDecimal grossMargin;
    /**
     * 委托保证金
     */
    private BigDecimal orderMargin;
    /**
     * 开仓手续费,成交时修改(可能部分成交，按比例收取)
     */
    private BigDecimal openFee;
    /**
     * 平仓手续费，在下委托时提前收取(可能部分成交，按比例收取)
     */
    private BigDecimal closeFee;

	/**
     * 已成交金额
     */
    private BigDecimal filledAmt;
    /**
	 * 已成交量
	 */
	private BigDecimal filledVolume;
	/**
     * @deprecated
     * 取消金额 (撤单时计算保存)：amt - filledAmt
     */
    private BigDecimal cancelAmt;


}