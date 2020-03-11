package com.hp.sh.expv3.pc.strategy.data;

import java.math.BigDecimal;

/**
 * 订单费用数据
 * @author wangjg
 *
 */
public interface OrderMargin {

	BigDecimal getOpenFee();

	BigDecimal getCloseFee();

	BigDecimal getOrderMargin();

}
