package com.hp.sh.expv3.bb.component;

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
	 * 获取开仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	BigDecimal getTakerFeeRatio(long userId, String asset, String symbol);

	/**
	 * 获取maker开仓手续费率
	 * 
	 * @param userId
	 * @return
	 */
	BigDecimal getMakerFeeRatio(long userId, String asset, String symbol);

}
