package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/24
 */
public class BbAccountLogExtVo implements Serializable {
    @ApiModelProperty("自增主键")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("币种")
    private String asset;

    @ApiModelProperty("成交时间")
    private Long tradeTime;

    @ApiModelProperty("类型")
    private Integer tradeType;

    @ApiModelProperty("数量")
    private BigDecimal volume;

    @ApiModelProperty("余额")
    private BigDecimal balance;

    @ApiModelProperty("手续费")
    private BigDecimal fee;

    @ApiModelProperty("关联其他表的主键id")
    private  Long refId;

    @ApiModelProperty("时间")
    private  Long time;

    @ApiModelProperty("创建时间")
    private  Long ctime;

    @Override
    public String toString() {
        return "BbAccountLogExtVo{" +
                "id=" + id +
                ", userId=" + userId +
                ", asset='" + asset + '\'' +
                ", tradeTime=" + tradeTime +
                ", tradeType=" + tradeType +
                ", volume=" + volume +
                ", balance=" + balance +
                ", fee=" + fee +
                ", refId=" + refId +
                ", time=" + time +
                '}';
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BbAccountLogExtVo() {
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

    public Long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
