package com.hp.sh.expv3.pc.strategy.aaab;

import java.math.BigDecimal;

import com.hp.sh.expv3.utils.math.Precision;

/**
 * 计算合成字段
 * 计价货币
 * 基础货币
 * 手续费、押金、收入  -- 计价货币
 * @author wangjg
 *
 */
public class AAABCompFieldCalc {
	
	/**
	 * 计算交易合约总金额 == fv/price（计价货币）
	 * @param volume 合约张数
	 * @param faceValue 面值(基础货币)
	 * @return`
	 */
	public BigDecimal calcAmount(BigDecimal volume, BigDecimal faceValue, BigDecimal price){
		BigDecimal fv = faceValue.multiply(volume);
		BigDecimal amount = fv.multiply(price);
		return amount;
	}

	/**
	 * 计算交易合约 基础货币总价值 == fv
	 */
	public static BigDecimal calcBaseValue(BigDecimal volume, BigDecimal faceValue){
		BigDecimal fv = faceValue.multiply(volume);
		return fv;
	}
	
}
