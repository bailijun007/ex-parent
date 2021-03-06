package com.hp.sh.expv3.pc.strategy.vo;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.data.OrderFeeParam;

public class OrderFeeParamVo implements OrderFeeParam {

	private BigDecimal faceValue;
	private BigDecimal volume;
	private BigDecimal price;
	private BigDecimal openFeeRatio;
	private BigDecimal closeFeeRatio;
	private BigDecimal marginRatio;
    private Integer closeFlag;

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

	public BigDecimal getOpenFeeRatio() {
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

	public Integer getCloseFlag() {
		return closeFlag;
	}

	public void setCloseFlag(Integer closeFlag) {
		this.closeFlag = closeFlag;
	}

}
