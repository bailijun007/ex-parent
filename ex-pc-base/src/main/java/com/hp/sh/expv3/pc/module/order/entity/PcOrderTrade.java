/**
 * @author 10086
 * @date 2019/10/30
 */
package com.hp.sh.expv3.pc.module.order.entity;

import java.math.BigDecimal;

import com.hp.sh.expv3.base.entity.BaseBizEntity;

/**
 * 永续合约_用户订单成交记录
 * 
 */
public class PcOrderTrade extends BaseBizEntity {

	// 账号ID
	private Long accountId;
	
	//订单ID
	private Long orderId;
	
	//交易ID
	private Long tradeId;

	// 1-marker， 0-taker
	private Integer makerFlag;

	//合约交易品种
	private String symbol;

	//资产
	private String asset;

	//成交价
	private BigDecimal price;

	//成交金额（单位：【计价货币】）
	private BigDecimal amt;

	//成交量（单位：【结算货币】）
	private BigDecimal volume;

	//成交时间
	private Long tradeTime;

	//手续费收取人
	private Long feeCollectorId;
	
	//手续费率
	private BigDecimal feeRatio;
	
	//手续费
	private BigDecimal fee;
	
	//盈亏
	private BigDecimal pnl;

}
