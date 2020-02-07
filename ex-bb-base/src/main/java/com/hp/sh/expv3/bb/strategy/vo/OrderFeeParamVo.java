package com.hp.sh.expv3.bb.strategy.vo;

import java.math.BigDecimal;

import com.hp.sh.expv3.bb.strategy.data.OrderFeeParam;

public class OrderFeeParamVo implements OrderFeeParam {

	private BigDecimal faceValue;
	private BigDecimal volume;
	private BigDecimal price;
	private BigDecimal openFeeRatio;
	private BigDecimal closeFeeRatio;
	private BigDecimal marginRatio;

	public OrderFeeParamVo() {
	}

	public BigDecimal getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(BigDecimal faceValue) {
		this.faceValue = faceValue;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getFeeRatio() {
		return openFeeRatio;
	}

	public void setOpenFeeRatio(BigDecimal openFeeRatio) {
		this.openFeeRatio = openFeeRatio;
	}

	public BigDecimal getCloseFeeRatio() {
		return closeFeeRatio;
	}

	public void setCloseFeeRatio(BigDecimal closeFeeRatio) {
		this.closeFeeRatio = closeFeeRatio;
	}

	public BigDecimal getMarginRatio() {
		return marginRatio;
	}

	public void setMarginRatio(BigDecimal marginRatio) {
		this.marginRatio = marginRatio;
	}

}
