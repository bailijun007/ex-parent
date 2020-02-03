package com.hp.sh.expv3.bb.strategy.data;

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
