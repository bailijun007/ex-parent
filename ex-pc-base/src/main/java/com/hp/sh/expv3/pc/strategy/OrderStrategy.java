package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.data.OrderMargin;
import com.hp.sh.expv3.pc.strategy.data.OrderFeeParam;
import com.hp.sh.expv3.pc.strategy.vo.OrderFeeData;

/**
 * 订单计算策略
 * @author wangjg
 *
 */
public interface OrderStrategy {
	
	BigDecimal calcAmount(BigDecimal volume, BigDecimal faceValue, BigDecimal price);
	
	BigDecimal calcBaseValue(BigDecimal volume, BigDecimal faceValue, BigDecimal price);

	OrderFeeData calcNewOrderFee(OrderFeeParam pcOrder);

	OrderFeeData calcRaitoFee(OrderMargin order, BigDecimal total, BigDecimal number);

	BigDecimal calMargin(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal marginRatio);

	BigDecimal calcTradeFee(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal feeRatio);
	
}
