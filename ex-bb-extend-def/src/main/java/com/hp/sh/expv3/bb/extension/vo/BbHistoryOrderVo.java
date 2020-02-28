package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/19
 */
public class BbHistoryOrderVo implements Serializable {
    //订单ID
    @ApiModelProperty("自增主键")
    private Long id;

    //用户ID
    @ApiModelProperty("用户ID")
    private Long userId;

    //资产
    @ApiModelProperty("资产")
    private String asset;

    //合约交易品种
    @ApiModelProperty("合约交易品种")
    private String symbol;

    // 创建时间
    @ApiModelProperty("创建时间")
    private Long created;

    @ApiModelProperty("委托数量，初始设置后，后续不会修改")
    private BigDecimal  volume;

    //是否:1-平仓,0-开
    @ApiModelProperty("是否:1-平仓,0-开")
    private Integer bidFlag;

    //杠杆
    @ApiModelProperty("杠杆")
    private BigDecimal leverage;

    //成交量
    @ApiModelProperty("成交量")
    private BigDecimal filledVolume;

    //成交比例
    @ApiModelProperty("成交比例")
    private BigDecimal filledRatio;

    //成交均价
    @ApiModelProperty("成交均价")
    private BigDecimal meanPrice;

    //委托价格
    @ApiModelProperty("委托价格")
    private BigDecimal price;

    //已收手续费
    @ApiModelProperty("已收手续费")
    private BigDecimal feeCost;

    //委托状态，OrderStatus#*
    @ApiModelProperty("委托状态")
    private Integer status;


    @ApiModelProperty("委托类型")
    private Integer  orderType;

    public BbHistoryOrderVo() {
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "BbHistoryOrderVo{" +
                "id=" + id +
                ", userId=" + userId +
                ", asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", created=" + created +
                ", bidFlag=" + bidFlag +
                ", leverage=" + leverage +
                ", filledVolume=" + filledVolume +
                ", filledRatio=" + filledRatio +
                ", meanPrice=" + meanPrice +
                ", price=" + price +
                ", feeCost=" + feeCost +
                ", status=" + status +
                ", orderType=" + orderType +
                '}';
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
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

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Integer getBidFlag() {
        return bidFlag;
    }

    public void setBidFlag(Integer bidFlag) {
        this.bidFlag = bidFlag;
    }

    public BigDecimal getLeverage() {
        return leverage;
    }

    public void setLeverage(BigDecimal leverage) {
        this.leverage = leverage;
    }

    public BigDecimal getFilledVolume() {
        return filledVolume;
    }

    public void setFilledVolume(BigDecimal filledVolume) {
        this.filledVolume = filledVolume;
    }

    public BigDecimal getFilledRatio() {
        return filledRatio;
    }

    public void setFilledRatio(BigDecimal filledRatio) {
        this.filledRatio = filledRatio;
    }

    public BigDecimal getMeanPrice() {
        return meanPrice;
    }

    public void setMeanPrice(BigDecimal meanPrice) {
        this.meanPrice = meanPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFeeCost() {
        return feeCost;
    }

    public void setFeeCost(BigDecimal feeCost) {
        this.feeCost = feeCost;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
