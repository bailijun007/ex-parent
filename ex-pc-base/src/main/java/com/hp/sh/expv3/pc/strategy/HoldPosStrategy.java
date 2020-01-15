package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;

/**
 * 持仓计算策略(仓位相关实时变化数据)
 * 
 * @author wangjg
 *
 */
public interface HoldPosStrategy {
	/**
	 * 计算收益
	 * @param longFlag 是否多仓
	 * @param amt 成交金额
	 * @param meanPrice 均价
	 * @param closePrice 平仓价
	 * @return
	 */
	public BigDecimal calcPnl(int longFlag, BigDecimal amt, BigDecimal meanPrice, BigDecimal closePrice) ;

	/**
	 * 成交均价
	 * @param isLong
	 * @param baseValue
	 * @param amt
	 * @return
	 */
    public BigDecimal calcMeanPrice(int longFlag, BigDecimal baseValue, BigDecimal amt) ;

	/**
	 * 计算仓位预估强评价
	 * @return
	 */
	public BigDecimal calcLiqPrice(int longFlag, BigDecimal amount, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin);

	/**
	 * 计算仓位保证金率
	 * 保证金率 =（保证金+未实现盈亏) / (合约面值*持仓张数／最新标记价格）
	 * @return
	 */
	public BigDecimal calPosMarginRatio(BigDecimal posMargin, BigDecimal posPnl, BigDecimal faceValue, BigDecimal volume, BigDecimal markPrice);

	/**
	 * 计算成交收益
	 * @param longFlag 
	 * @param amount 交易金额
	 * @param meanPrice 开仓均价
	 * @param price 成交价
	 * @return
	 */
	public BigDecimal calcTradePnl(int longFlag, BigDecimal amount, BigDecimal meanPrice, BigDecimal price);

	/**
	 * 计算仓位的未实现盈亏(浮动盈亏)
	 * @return
	 */
	public BigDecimal calcPosPnl(int longFlag, BigDecimal amount, BigDecimal meanPrice, BigDecimal markPrice);

	/**
	 * 用 均价 标记价格 未实现盈亏 计算 仓位保证金
	 * @param longFlag
	 * @param initMarginRatio
	 * @param amount
	 * @param feeRatio
	 * @param meanPrice
	 * @param markPrice
	 * @return
	 */
	public BigDecimal calcInitMargin(Integer longFlag, BigDecimal initMarginRatio, BigDecimal amount, BigDecimal meanPrice, BigDecimal markPrice) ;

	/**
	 * 计算破产价
	 * @param isLong
	 * @param openPrice
	 * @param amt
	 * @param margin
	 * @return
	 */
	public BigDecimal calcBankruptPrice(Integer longFlag, BigDecimal openPrice, BigDecimal amt, BigDecimal margin) ;

}
