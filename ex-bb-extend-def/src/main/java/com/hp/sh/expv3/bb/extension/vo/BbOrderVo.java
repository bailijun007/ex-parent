package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public class BbOrderVo implements Serializable {

    @ApiModelProperty("自增主键")
    private Long id;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("资产类型")
    private String asset;

    @ApiModelProperty("合约交易品种")
    private String symbol;

    @ApiModelProperty("买卖:1-买,0-卖")
    private Integer bidFlag;

    @ApiModelProperty("委托价格")
    private BigDecimal price;

    @ApiModelProperty("委托类型")
    private Integer orderType;

    @ApiModelProperty("委托数量（个）")
    private BigDecimal volume;

    @ApiModelProperty("委托状态")
    private Integer status;

    @ApiModelProperty("手续费率")
    private BigDecimal feeRatio;

    @ApiModelProperty("实收手续费,成交后累加")
    private BigDecimal feeCost;

    @ApiModelProperty("委托押金")
    private BigDecimal orderMargin;

    @ApiModelProperty("开仓手续费,成交时减少")
    private BigDecimal fee;

    @ApiModelProperty("已成交量（单位：个）")
    private BigDecimal filledVolume;

    @ApiModelProperty("取消（单位：个）")
    private BigDecimal cancelVolume;

    @ApiModelProperty("委托有效时间")
    private Integer timeInForce;

    @ApiModelProperty("取消时间")
    private long cancelTime;

    @ApiModelProperty("是否活动委托")
    private Integer activeFlag;

    @ApiModelProperty("创造委托订单操作员")
    private String createOperator;

    @ApiModelProperty("取消委托订单操作员")
    private String cancelOperator;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("客户自定义委托ID，用于与客户系统关联 （open api）")
    private String clientOrderId;

    @ApiModelProperty("版本")
    private Long version;

    @ApiModelProperty("杠杆")
    private BigDecimal leverage;

    @ApiModelProperty("委托保证金资产")
    private String orderMarginCurrency;


    @ApiModelProperty("修改时间")
    private Long modified;

    @ApiModelProperty("创建时间")
    private Long created;

    public BbOrderVo() {
    }

    @Override
    public String toString() {
        return "BbOrderVo{" +
                "id=" + id +
                ", userId=" + userId +
                ", asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", bidFlag=" + bidFlag +
                ", price=" + price +
                ", orderType=" + orderType +
                ", volume=" + volume +
                ", status=" + status +
                ", feeRatio=" + feeRatio +
                ", feeCost=" + feeCost +
                ", orderMargin=" + orderMargin +
                ", fee=" + fee +
                ", filledVolume=" + filledVolume +
                ", cancelVolume=" + cancelVolume +
                ", timeInForce=" + timeInForce +
                ", cancelTime=" + cancelTime +
                ", activeFlag=" + activeFlag +
                ", createOperator='" + createOperator + '\'' +
                ", cancelOperator='" + cancelOperator + '\'' +
                ", remark='" + remark + '\'' +
                ", clientOrderId='" + clientOrderId + '\'' +
                ", version=" + version +
                ", leverage=" + leverage +
                ", orderMarginCurrency='" + orderMarginCurrency + '\'' +
                ", modified=" + modified +
                ", created=" + created +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getBidFlag() {
        return bidFlag;
    }

    public void setBidFlag(Integer bidFlag) {
        this.bidFlag = bidFlag;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getFeeRatio() {
        return feeRatio;
    }

    public void setFeeRatio(BigDecimal feeRatio) {
        this.feeRatio = feeRatio;
    }

    public BigDecimal getFeeCost() {
        return feeCost;
    }

    public void setFeeCost(BigDecimal feeCost) {
        this.feeCost = feeCost;
    }

    public BigDecimal getOrderMargin() {
        return orderMargin;
    }

    public void setOrderMargin(BigDecimal orderMargin) {
        this.orderMargin = orderMargin;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFilledVolume() {
        return filledVolume;
    }

    public void setFilledVolume(BigDecimal filledVolume) {
        this.filledVolume = filledVolume;
    }

    public BigDecimal getCancelVolume() {
        return cancelVolume;
    }

    public void setCancelVolume(BigDecimal cancelVolume) {
        this.cancelVolume = cancelVolume;
    }

    public Integer getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(Integer timeInForce) {
        this.timeInForce = timeInForce;
    }

    public long getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(long cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public String getCancelOperator() {
        return cancelOperator;
    }

    public void setCancelOperator(String cancelOperator) {
        this.cancelOperator = cancelOperator;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public BigDecimal getLeverage() {
        return leverage;
    }

    public void setLeverage(BigDecimal leverage) {
        this.leverage = leverage;
    }

    public String getOrderMarginCurrency() {
        return orderMarginCurrency;
    }

    public void setOrderMarginCurrency(String orderMarginCurrency) {
        this.orderMarginCurrency = orderMarginCurrency;
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
}
