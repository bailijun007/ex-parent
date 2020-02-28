package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/20
 */
public class BbAccountExtVo extends  BbAccountVo{
    @ApiModelProperty("总额")
    private BigDecimal total;

    @ApiModelProperty("可用余额")
    private BigDecimal available;

    @ApiModelProperty("冻结资产")
    private BigDecimal lock;

    public BbAccountExtVo() {
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

    public BigDecimal getLock() {
        return lock;
    }

    public void setLock(BigDecimal lock) {
        this.lock = lock;
    }
}
