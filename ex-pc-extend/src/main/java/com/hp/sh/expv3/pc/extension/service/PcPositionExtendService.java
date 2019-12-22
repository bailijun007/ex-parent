package com.hp.sh.expv3.pc.extension.service;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcPositionExtendService {
	/**
	 * 获取仓位保证金
	 * @param userId
	 * @param asset
	 * @return
	 */
    BigDecimal getPosMargin(Long userId, String asset);
    
	/**
	 * 查询收益(已实现盈亏)
     * select sum(pnl) from pc_order_trade where pos_id = ? and asset = ? and user_id = ?
	 * @param userId
	 * @param asset
	 * @param posId 仓位ID
	 * @return
	 */
    BigDecimal getPl(Long userId, String asset, Long posId);
    
	/**
	 * 查询收益率
     * getPl/pos.initMargin
     * ? = ?
	 * @param userId
	 * @param asset
	 * @param posId 仓位ID
	 * @return
	 */
    BigDecimal getPlRatio(Long userId, String asset, Long posId);
}