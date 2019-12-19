package com.hp.sh.expv3.pc.extension.service;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcPositionCoreService2 {
	/**
	 * 查询收益(已实现盈亏)
	 * @param userId
	 * @param asset
	 * @param posId 仓位ID
	 * @return
	 */
    BigDecimal getPl(Long userId, String asset, Long posId);
    
	/**
	 * 查询收益率
	 * @param userId
	 * @param asset
	 * @param posId 仓位ID
	 * @return
	 */
    BigDecimal getPlRatio(Long userId, String asset, Long posId);
}
