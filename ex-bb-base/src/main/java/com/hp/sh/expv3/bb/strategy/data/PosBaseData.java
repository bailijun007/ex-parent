package com.hp.sh.expv3.bb.strategy.data;

import java.math.BigDecimal;

/**
 * 仓位基本数据
 * @author wangjg
 *
 */
public interface PosBaseData {

	public long getUserId();

	public String getAsset();

	public String getSymbol();

	public Integer getLongFlag();

	public BigDecimal getLeverage();

	public BigDecimal getVolume();

}
