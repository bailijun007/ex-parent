package com.hp.sh.expv3.pc.extension.vo;

import java.math.BigDecimal;

/**
 */
public class PcPositionNumStatVo {

	//多空
	private Integer longFlag;

	//张数
	private BigDecimal posNum;

	public Integer getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(Integer longFlag) {
		this.longFlag = longFlag;
	}

	public BigDecimal getPosNum() {
		return posNum;
	}

	public void setPosNum(BigDecimal posNum) {
		this.posNum = posNum;
	}
		
}
