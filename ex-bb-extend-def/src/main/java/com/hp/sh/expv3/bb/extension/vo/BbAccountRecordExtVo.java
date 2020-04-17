package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/24
 */
public class BbAccountRecordExtVo implements Serializable {
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

    @ApiModelProperty("关联对象的ID(无效)")
    private Long associatedId;


    @ApiModelProperty("关联对象的ID（有效）")
    private String tradeNo;

    @ApiModelProperty("创建时间")
    private  Long ctime;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Override
    public String toString() {
        return "BbAccountRecordExtVo{" +
                "id=" + id +
                ", userId=" + userId +
                ", asset='" + asset + '\'' +
                ", tradeTime=" + tradeTime +
                ", tradeType=" + tradeType +
                ", volume=" + volume +
                ", balance=" + balance +
                ", fee=" + fee +
                ", associatedId=" + associatedId +
                ", tradeNo='" + tradeNo + '\'' +
                ", ctime=" + ctime +
                '}';
    }

    public Long getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(Long associatedId) {
        this.associatedId = associatedId;
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

    public BbAccountRecordExtVo() {
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

}
