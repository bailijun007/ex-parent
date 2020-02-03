package com.hp.sh.expv3.bb.strategy.data;

import java.math.BigDecimal;

/**
 * 仓位数据，包含累计数据
 * @author wangjg
 *
 */
public interface PosData extends PosBaseData{

	public BigDecimal getMeanPrice();

}
