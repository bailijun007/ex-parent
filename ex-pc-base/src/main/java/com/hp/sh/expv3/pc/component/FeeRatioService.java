package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import com.hp.sh.expv3.dev.Question;

/**
 * 查询保证金费率，redis或db
 * 
 * @author lw
 *
 */
@Question(ask="是否随玩法不同", answer="NO")
public interface FeeRatioService {

	/**
	 * 获取初始化保证金率
	 * 
	 * @param leverage
	 * @return
	 */
	BigDecimal getInitedMarginRatio(BigDecimal leverage);

	/**
	 * 获取开仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	BigDecimal getOpenFeeRatio(long userId, String asset, String symbol);

	/**
	 * 平仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	BigDecimal getCloseFeeRatio(long userId, String asset, String symbol);

	/**
	 * 维持保证金率
	 * @param userId
	 * @param asset
	 * @param symbol
	 * @param volume
	 * @return
	 */
	BigDecimal getHoldRatio(Long userId, String asset, String symbol, BigDecimal volume);

	/**
	 * 获取maker开仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	BigDecimal getMakerOpenFeeRatio(long userId, String asset, String symbol);

	/**
	 * 获取maker平仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	BigDecimal getMakerCloseFeeRatio(long userId, String asset, String symbol);

	default BigDecimal getMaxLeverage(Long userId, String asset, String symbol, BigDecimal posVolume){
		return new BigDecimal(50);
	}

}
