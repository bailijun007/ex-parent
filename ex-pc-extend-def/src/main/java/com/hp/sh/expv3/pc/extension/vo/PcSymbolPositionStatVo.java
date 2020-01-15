package com.hp.sh.expv3.pc.extension.vo;

import java.math.BigDecimal;

/**
 * 合约仓位统计
 * @author wangjg
 *
 */
public class PcSymbolPositionStatVo {

	//多仓
	private Integer longFlag;

	//空仓
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
