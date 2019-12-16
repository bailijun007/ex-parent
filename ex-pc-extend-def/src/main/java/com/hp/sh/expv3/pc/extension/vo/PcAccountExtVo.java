package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@ApiModel("获取合约账户Vo")
public class PcAccountExtVo implements Serializable {
    @ApiModelProperty("账户货币")
    private String asset;

    @ApiModelProperty("总额")
    private BigDecimal total;

    @ApiModelProperty("可用余额")
    private BigDecimal available;

    @ApiModelProperty("账户id")
    private  Long accountId;

    @ApiModelProperty("委托保证金")
    private  BigDecimal orderMargin;

    @ApiModelProperty("仓位保证金")
    private  BigDecimal poserMargin;

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getOrderMargin() {
        return orderMargin;
    }

    public void setOrderMargin(BigDecimal orderMargin) {
        this.orderMargin = orderMargin;
    }

    public BigDecimal getPoserMargin() {
        return poserMargin;
    }

    public void setPoserMargin(BigDecimal poserMargin) {
        this.poserMargin = poserMargin;
    }

    public PcAccountExtVo() {
    }
}
