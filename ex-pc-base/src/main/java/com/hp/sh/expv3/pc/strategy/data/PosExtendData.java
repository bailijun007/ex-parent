package com.hp.sh.expv3.pc.strategy.data;

import java.math.BigDecimal;

/**
 * 仓位扩展数据，包含transient字段
 * @author wangjg
 *
 */
public interface PosExtendData extends PosData{

	public BigDecimal getAmount();

	public BigDecimal getMarkPrice();

}
