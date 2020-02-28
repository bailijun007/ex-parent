package com.hp.sh.expv3.pc.strategy.data;

import java.math.BigDecimal;

/**
 * 仓位数据，包含累计数据
 * @author wangjg
 *
 */
public interface PosData extends PosBaseData{

	public BigDecimal getMeanPrice();

	public BigDecimal getHoldMarginRatio();

}
