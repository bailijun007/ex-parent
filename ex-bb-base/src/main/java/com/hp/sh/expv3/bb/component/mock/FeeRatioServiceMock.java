package com.hp.sh.expv3.bb.component.mock;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.FeeRatioService;

/**
 * 查询保证金费率，redis或db
 * 
 * @author lw
 *
 */
@Component
public class FeeRatioServiceMock implements FeeRatioService {

	@Override
	public BigDecimal getTakerFeeRatio(long userId, String asset, String symbol) {
		return getFeeRatio(userId, false);
	}

	/**
	 * 获取开仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	private BigDecimal getFeeRatio(long userId, boolean isMaker) {
		if (isMaker) {
			return new BigDecimal("0.001");
		} else {
			return new BigDecimal("0.0015");
		}
	
	}
	
	@Override
	public BigDecimal getMakerFeeRatio(long userId, String asset, String symbol) {
		return getFeeRatio(userId, true);
	}

}
