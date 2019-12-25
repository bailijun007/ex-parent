package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;

/**
 * 持仓计算策略(仓位相关实时变化数据)
 * @author wangjg
 *
 */
public interface HoldPosStrategy {

	BigDecimal calcPnl(int longFlag, BigDecimal amt, BigDecimal entryPrice, BigDecimal closePrice);

	BigDecimal calcMeanPrice(int longFlag, BigDecimal baseValue, BigDecimal amt);

	BigDecimal calcLiqPrice(int longFlag, BigDecimal amount, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin);

	BigDecimal calcBankruptPrice(Integer longFlag, BigDecimal openPrice, BigDecimal amt, BigDecimal margin);

	BigDecimal calcInitMargin(Integer longFlag, BigDecimal initMarginRatio, BigDecimal amount, BigDecimal feeRatio, BigDecimal meanPrice, BigDecimal markPrice);

	BigDecimal calcPosLiqPrice(int longFlag, BigDecimal amount, BigDecimal meanPrice, BigDecimal holdMarginRatio, BigDecimal posMargin);

	BigDecimal calcPosPnl(int longFlag, BigDecimal amount, BigDecimal meanPrice, BigDecimal markPrice);

	BigDecimal calcTradePnl(int longFlag, BigDecimal amount, BigDecimal meanPrice, BigDecimal price);

	BigDecimal calPosMarginRatio(BigDecimal posMargin, BigDecimal posPnl, BigDecimal faceValue, BigDecimal volume, BigDecimal markPrice);

}
