package com.hp.sh.expv3.pc.strategy.vo;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.data.MarginParam;

public class PosMarginVo implements MarginParam {

	private	BigDecimal openFee;
	
	private	BigDecimal closeFee;
	
	private	BigDecimal orderMargin;
	
	public PosMarginVo() {
		super();
	}

	public PosMarginVo(BigDecimal orderMargin, BigDecimal openFee, BigDecimal closeFee) {
		this.openFee = openFee;
		this.closeFee = closeFee;
		this.orderMargin = orderMargin;
	}

	public BigDecimal getOpenFee() {
		return openFee;
	}

	public void setOpenFee(BigDecimal openFee) {
		this.openFee = openFee;
	}

	public BigDecimal getCloseFee() {
		return closeFee;
	}

	public void setCloseFee(BigDecimal closeFee) {
		this.closeFee = closeFee;
	}

	public BigDecimal getOrderMargin() {
		return orderMargin;
	}

	public void setOrderMargin(BigDecimal orderMargin) {
		this.orderMargin = orderMargin;
	}

}
