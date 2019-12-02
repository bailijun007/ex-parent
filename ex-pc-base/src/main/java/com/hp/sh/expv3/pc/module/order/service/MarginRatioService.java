package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

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
        return BigDecimal.ONE.divide(leverage, Precision.DIVIDE_SCALE, Precision.LESS);
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
	
}
