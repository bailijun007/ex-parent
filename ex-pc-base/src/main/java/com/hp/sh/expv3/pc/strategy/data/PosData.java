package com.hp.sh.expv3.pc.strategy.data;

import java.math.BigDecimal;

/**
 * 仓位数据
 * @author wangjg
 *
 */
public interface PosData extends PosBaseData{

	public long getUserId();

	public String getAsset();

	public String getSymbol();

	public Integer getLongFlag();

	public BigDecimal getLeverage();

	public BigDecimal getVolume();

	public BigDecimal getMeanPrice();

}
