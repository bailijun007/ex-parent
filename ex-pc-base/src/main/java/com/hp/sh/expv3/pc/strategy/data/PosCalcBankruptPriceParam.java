package com.hp.sh.expv3.pc.strategy.data;

import java.math.BigDecimal;

/**
 * 仓位基本数据
 * @author wangjg
 *
 */
public interface PosCalcBankruptPriceParam {

	public String getAsset();

	public String getSymbol();

	public Integer getLongFlag();

	public BigDecimal getVolume();

	public BigDecimal getFaceValue();

	public BigDecimal getPosMargin();

	public BigDecimal getMeanPrice();
	
}
