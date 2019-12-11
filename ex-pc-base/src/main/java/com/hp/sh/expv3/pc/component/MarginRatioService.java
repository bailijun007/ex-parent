package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.hp.sh.expv3.dev.Question;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 查询获取保证金，redis或db
 * 
 * @author lw
 *
 */
@Question("是否随玩法不同")
@Service
public class MarginRatioService {

	/**
	 * 获取初始化保证金率
	 * 
	 * @param leverage
	 * @return
	 */
	public BigDecimal getInitedMarginRatio(BigDecimal leverage) {
		return BigDecimal.ONE.divide(leverage, Precision.PERCENT_PRECISION, Precision.LESS);
	}

	/**
	 * 获取开仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getOpenFeeRatio(long userId) {
		return getFeeRatio(userId, IntBool.NO);
	}

	/**
	 * 平仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getCloseFeeRatio(long userId) {
		return getFeeRatio(userId, IntBool.NO);
	}

	/**
	 * 获取开仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	private BigDecimal getFeeRatio(long userId, Integer makerFlag) {
		if (IntBool.isTrue(makerFlag)) {
			// TODO get from cache
			return new BigDecimal("0.0025");
		} else {
			return new BigDecimal("0.0075");
		}
	
	}

	public BigDecimal getHoldRatio(Long userId, String asset, String symbol, BigDecimal volume) {
		return new BigDecimal("0.005");
	}

}
