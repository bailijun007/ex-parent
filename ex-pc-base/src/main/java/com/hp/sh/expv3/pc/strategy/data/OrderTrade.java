package com.hp.sh.expv3.pc.strategy.data;

import java.math.BigDecimal;

/**
 * 订单成交数据
 * @author wangjg
 *
 */
public interface OrderTrade {

	/**
	 * 成交张数
	 * @return
	 */
	BigDecimal getVolume();

	/**
	 * 成交价格
	 * @return
	 */
	BigDecimal getPrice();

}
