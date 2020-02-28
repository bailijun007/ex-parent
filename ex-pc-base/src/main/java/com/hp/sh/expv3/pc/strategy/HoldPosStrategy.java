package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.data.PosData;

/**
 * 持仓计算策略(仓位相关实时变化数据)
 * 
 * @author wangjg
 *
 */
public interface HoldPosStrategy {
	
	/**
	 * 成交均价
	 * @param isLong
	 * @param baseValue
	 * @param amt
	 * @return
	 */
    public BigDecimal calcMeanPrice(int longFlag, BigDecimal baseValue, BigDecimal amt) ;

	public BigDecimal calcLiqPrice(PosData pos);

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
	 * 计算收益
	 * @param longFlag 是否多仓
	 * @param amt 成交金额
	 * @param meanPrice 均价
	 * @param closePrice 平仓价
	 * @return
	 */
	public BigDecimal calcPnl(int longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal openPrice, BigDecimal closePrice) ;

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
	public BigDecimal calcInitMargin(Integer longFlag, BigDecimal initMarginRatio, BigDecimal volume, BigDecimal faceValue, BigDecimal meanPrice, BigDecimal markPrice) ;

	/**
	 * 计算破产价
	 * @param isLong
	 * @param openPrice
	 * @param amt
	 * @param margin
	 * @return
	 */
	BigDecimal calcBankruptPrice(Integer longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal margin, BigDecimal openPrice);

}
