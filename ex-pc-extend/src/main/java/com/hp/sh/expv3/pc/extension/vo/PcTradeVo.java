package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public class PcTradeVo implements Serializable {
    @ApiModelProperty("自增主键")
    private Long id;

    @ApiModelProperty("资产")
    private String asset;

    @ApiModelProperty("事务Id")
    private Long matchTxId;

    @ApiModelProperty("taker是否买：1-是，0-否")
    private Integer tkBidFlag;

    @ApiModelProperty("taker账户ID")
    private Long tkAccountId;

    @ApiModelProperty("taker订单ID")
    private Long tkOrderId;

    @ApiModelProperty("taker是否平仓")
    private Integer tkCloseFlag;

    @ApiModelProperty("maker账户Id")
    private Long mkAccountId;

    @ApiModelProperty("maker订单ID")
    private Long mkOrderId;

//    @ApiModelProperty("taker是否平仓")
//    private Integer tkCloseFlag;



}
