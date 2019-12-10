package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MarginMode;

@Component
public class PcDefaultSymbolSetting {

	public Integer getMarginMode() {
		return MarginMode.FIXED;
	};

	public BigDecimal getCrossLeverage() {
		return new BigDecimal(5);
	};

	public BigDecimal getMaxLeverage() {
		return new BigDecimal(50);
	};

	public BigDecimal getLongLeverage() {
		return new BigDecimal(5);
	};

	public BigDecimal getShortLeverage() {
		return new BigDecimal(5);
	};

	@Deprecated
	public BigDecimal getHoldMarginRatio() {
		return new BigDecimal("0.0005");
	};

}
