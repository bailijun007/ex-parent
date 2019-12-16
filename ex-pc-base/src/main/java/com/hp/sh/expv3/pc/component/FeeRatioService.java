package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitee.hupadev.commons.cache.Cache;
import com.hp.sh.expv3.dev.Question;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 查询保证金费率，redis或db
 * 
 * @author lw
 *
 */
@Question(ask="是否随玩法不同", answer="NO")
@Service
public class FeeRatioService {

	@Autowired
	private Cache cache;

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
		return getFeeRatio(userId, false);
	}

	/**
	 * 平仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
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

	public BigDecimal getHoldRatio(Long userId, String asset, String symbol, BigDecimal volume) {
		return new BigDecimal("0.005");
	}
	
	int ___________;
	

	/**
	 * 获取maker开仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getMakerOpenFeeRatio(long userId) {
		return getFeeRatio(userId, true);
	}

	/**
	 * 获取maker平仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getMakerCloseFeeRatio(long userId) {
		return getFeeRatio(userId, true);
	}

}
