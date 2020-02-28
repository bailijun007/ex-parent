package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import com.hp.sh.expv3.utils.math.Precision;

/**
 * AABB计算合成字段
 * 计价货币
 * 基础货币
 * 手续费、押金、收入  -- 基础货币
 * @author wangjg
 *
 */
public class AABBCompFieldCalc {
	
	/**
	 * 计算交易合约总金额
	 * @param volume 合约张数
	 * @param faceValue 面值
	 * @return`
	 */
	public static BigDecimal calcAmount(BigDecimal volume, BigDecimal faceValue){
		BigDecimal fv = faceValue.multiply(volume);
		return fv;
	}

	/**
	 * 计算交易合约 基础货币总价值
	 * @param amount 交易总金额
	 * @param price 成交价格
	 * @return
	 */
	public static BigDecimal calcBaseValue(BigDecimal amount, BigDecimal price){
		return amount.divide(price, Precision.COMMON_PRECISION, Precision.LESS);
	}

	/**
	 * 计算交易合约 基础货币总价值
	 * @param volume 合约张数
	 * @param faceValue 面值
	 * @param price 成交价格
	 * @return
	 */
	public static BigDecimal calcBaseValue(BigDecimal volume, BigDecimal faceValue, BigDecimal price){
		return calcBaseValue(calcAmount(volume, faceValue), price);
	}
	
}
