/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.pc.module.position.entity;

import java.math.BigDecimal;

import com.hp.sh.expv3.base.entity.BaseBizEntity;

/**
 * 永续合约_仓位
 */
public class PcPosition extends BaseBizEntity {

	// 账号ID（注册用户ID）
    private Long accountId;
    
    // 合约交易品种
    private String symbol;
    
    // 资产
    private String asset;

    // 触发强平的标记价格
    private BigDecimal liqMarkPrice;

    // 触发强平的标记时间
    private Long liqMarkTime;

    // 仓位强平状态，0：未触发平仓，1：仓位被冻结，
    private Integer liqStatus;
    
    // 是否多仓
    private Integer longFlag;
    
    // 保证金模式:1-全仓,2-逐仓
    private int marginMode;
    
    // 仓位，合约金额
    private BigDecimal amt;

    // 最新杠杆
    private BigDecimal leverage;
    
    // 开仓杠杆 @see PcOrder#leverage
    private BigDecimal entryLeverage;
    
    /*
     * 币的数量，用于计算均价.
     * 建仓加仓时直接累加，算出均价
     * 平仓时均价不变，由 amt / price 得到最新的量，平仓时均价是不变的，价差是利润；也可以按照amt变化同比例减少
     */
    private BigDecimal volume;
    
    /**
     * 均价，仓位为0时，表示最后一次仓位变动时的均价
     */
    private BigDecimal entryPrice;
    
    /**
     * 仓位保证金， （ 基础维持保证金 ，包括用户充入的保证金 ）
     */
    private BigDecimal posMargin;
    
    /**
     * 强平价，仓位为0时，表示最后一次仓位变动时的强平价
     */
    private BigDecimal liqPrice;
    
    /**
     * 破产价，仓位为0时，表示最后一次仓位变动时的破产价
     */
    private BigDecimal bankruptPrice;
    
    /**
     * 维持保证金比率
     */
    private BigDecimal holdRatio;
    
    /**
     * 初始保证金，平仓的时候要减去对应的比例，以维持收益率一致
     */
    private BigDecimal initMargin;
    
    // 已实现盈亏
    private BigDecimal realisedPnl;
    
    // 是否自动追加保证金标识
    private Integer autoAddFlag;

    // 手续费
    private BigDecimal feeCost;

    //备注
    private String remark;


}
