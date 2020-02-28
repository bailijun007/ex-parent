package com.hp.sh.expv3.bb.strategy.data;

import java.math.BigDecimal;

/**
 * 订单费用数据
 * @author wangjg
 *
 */
public interface OrderFeeParam {

	BigDecimal getVolume();

	BigDecimal getPrice();

	BigDecimal getFeeRatio();

	BigDecimal getMarginRatio();
	
}
