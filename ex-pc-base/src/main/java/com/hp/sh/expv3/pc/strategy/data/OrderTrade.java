package com.hp.sh.expv3.pc.strategy.data;

import java.math.BigDecimal;

/**
 * 订单成交数据
 * @author wangjg
 *
 */
public interface OrderTrade {

	BigDecimal getVolume();

	BigDecimal getPrice();

}
