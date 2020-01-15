package com.hp.sh.expv3.pc.extension.vo;

import java.math.BigDecimal;

/**
 */
public class PcPositionVolumeStatVo {

	//多空
	private Integer longFlag;

	//张数
	private BigDecimal volume;

	public Integer getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(Integer longFlag) {
		this.longFlag = longFlag;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
		
}
