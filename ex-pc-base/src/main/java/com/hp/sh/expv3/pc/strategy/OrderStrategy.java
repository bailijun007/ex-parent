package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
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

	OrderFeeData calcRaitoAmt(PcOrder order, BigDecimal number);

	OrderFeeData calcNewOrderAmt(OrderFeeParam pcOrder);

	BigDecimal calMargin(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal marginRatio);
	
}
