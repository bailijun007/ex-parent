package com.hp.sh.expv3.fund.extension.vo;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 充值地址实体类
 *
 * @author BaiLiJun  on 2019/12/13
 */

@ApiModel("获取资金账户Vo")
public class CapitalAccountVo implements Serializable {

    @ApiModelProperty("账户id")
    private Long accountId;

    @ApiModelProperty("货币")
    private String asset;

    @ApiModelProperty("冻结资产")
    private BigDecimal lock;

    @ApiModelProperty("可用资产")
    private BigDecimal available;

    @ApiModelProperty("总资产")
    private BigDecimal totalAssets;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public BigDecimal getLock() {
        return lock;
    }

    public void setLock(BigDecimal lock) {
        this.lock = lock;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public CapitalAccountVo() {
    }
}
