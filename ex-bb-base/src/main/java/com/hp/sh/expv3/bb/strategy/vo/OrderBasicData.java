package com.hp.sh.expv3.bb.strategy.vo;

import java.math.BigDecimal;

/**
 * 订单基本数据
 * @author wangjg
 *
 */
public interface OrderBasicData {

	public String getAsset();

	public String getSymbol();

	public Integer getCloseFlag();

	public Integer getLongFlag();

	public BigDecimal getLeverage();

	public BigDecimal getVolume();

	public BigDecimal getFaceValue();

	public BigDecimal getPrice();
	
	///////////////////////////////////////////////////////////////

//	public void setAsset(String asset);
//
//	public void setSymbol(String symbol);
//
//	public void setCloseFlag(Integer closeFlag);
//
//	public void setLongFlag(Integer longFlag);
//
//	public void setLeverage(BigDecimal leverage);
//
//	public void setVolume(BigDecimal volume);
//
//	public void setFaceValue(BigDecimal faceValue);
//
//	public void setPrice(BigDecimal price);
	
}
