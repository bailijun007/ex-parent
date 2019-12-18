package com.hp.sh.expv3.pc.component.mock;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.cache.Cache;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.Precision;

/**
 * 查询保证金费率，redis或db
 * 
 * @author lw
 *
 */
@Primary
@Component
public class FeeRatioServiceImpl implements FeeRatioService {

	@Autowired
	private Cache cache;

	@Override
	public BigDecimal getInitedMarginRatio(BigDecimal leverage) {
		return BigDecimal.ONE.divide(leverage, Precision.PERCENT_PRECISION, Precision.LESS);
	}

	@Override
	public BigDecimal getOpenFeeRatio(long userId) {
		return getFeeRatio(userId, false);
	}

	@Override
	public BigDecimal getCloseFeeRatio(long userId) {
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
			// TODO get from cache
			return new BigDecimal("0.0025");
		} else {
			return new BigDecimal("0.0075");
		}
	
	}

	@Override
	public BigDecimal getHoldRatio(Long userId, String asset, String symbol, BigDecimal volume) {
		return new BigDecimal("0.005");
	}
	
	int ___________;
	
	@Override
	public BigDecimal getMakerOpenFeeRatio(long userId) {
		return getFeeRatio(userId, true);
	}

	@Override
	public BigDecimal getMakerCloseFeeRatio(long userId) {
		return getFeeRatio(userId, true);
	}

}
