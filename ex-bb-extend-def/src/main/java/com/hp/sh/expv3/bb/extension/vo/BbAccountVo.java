package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/13
 */
public class BbAccountVo {
    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("资产类型")
    private String asset;

    @ApiModelProperty("余额")
    private BigDecimal balance;

    @ApiModelProperty("版本")
    private Long version;

    @ApiModelProperty("修改时间")
    private Long modified;

    @ApiModelProperty("创建时间")
    private Long created;

    public BbAccountVo() {
    }

    public BbAccountVo(Long userId, String asset, BigDecimal balance, Long version, Long modified, Long created) {
        this.userId = userId;
        this.asset = asset;
        this.balance = balance;
        this.version = version;
        this.modified = modified;
        this.created = created;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "BbAccountVo{" +
                "userId=" + userId +
                ", asset='" + asset + '\'' +
                ", balance=" + balance +
                ", version=" + version +
                ", modified=" + modified +
                ", created=" + created +
                '}';
    }
}
