package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public class CurrentUserOrderVo implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("委托状态")
    private Integer status;

    @ApiModelProperty("手续费")
    private BigDecimal fee;

    @ApiModelProperty("委托价")
    private BigDecimal price;

    @ApiModelProperty("量")
    private BigDecimal qty;

    @ApiModelProperty("是否：1-多仓，0-空仓")
    private BigDecimal longFlag;

    @ApiModelProperty("杠杆")
    private BigDecimal leverage;

    @ApiModelProperty("资产")
    private String asset;

    @ApiModelProperty("交易对")
    private String symol;

    @ApiModelProperty("委托创建时间")
    private Long ctime;

    @ApiModelProperty("平均价")
    private BigDecimal avgPrice;

    @ApiModelProperty("已成交量")
    private BigDecimal filled_qty;


}
