package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.hp.sh.expv3.pc.constant.Precision;

/**
 * 费率,查询获取，redis或db
 * @author lw
 *
 */
@Service
public class MarginRatioService {
	
	/**
	 * 初始化保证金率
	 * @param leverage
	 * @return
	 */
    public BigDecimal getInitedMarginRatio(BigDecimal leverage) {
        return BigDecimal.ONE.divide(leverage, Precision.PERCENT_PRECISION, Precision.LESS);
    }

    /**
     * 开仓手续费率
     * @param userId
     * @return
     */
	public BigDecimal getOpenFeeRatio(long userId) {
		 return new BigDecimal("0.0075");
	}

	/**
	 * 平仓手续费率
	 * @param userId
	 * @return
	 */
	public BigDecimal getCloseFeeRatio(long userId) {
		return new BigDecimal("0.0075");
	}

	public BigDecimal getHoldRatio(Long userId, String asset, String symbol, BigDecimal volume) {
		return new BigDecimal("0.005");
	}
	
}
