package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MarginMode;

/**
 * 获取默认值
 * TOOD 取缓存
 * @author wangjg
 *
 */
@Component
public class PcDefaultSymbolSetting {

	public Integer getMarginMode() {
		return MarginMode.FIXED;
	};

	public BigDecimal getCrossLeverage(String asset, String symbol) {
		return new BigDecimal(10);
	};

	public BigDecimal getLongLeverage(String asset, String symbol) {
		return new BigDecimal(10);
	};

	public BigDecimal getShortLeverage(String asset, String symbol) {
		return new BigDecimal(10);
	};

}
