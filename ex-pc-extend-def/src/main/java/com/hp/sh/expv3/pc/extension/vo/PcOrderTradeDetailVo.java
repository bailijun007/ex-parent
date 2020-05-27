package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/24
 */
public class PcOrderTradeDetailVo implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("资产")
    private String asset;

    @ApiModelProperty("交易对")
    private String symbol;

    @ApiModelProperty("成交时间")
    private Long tradeTime;

    @ApiModelProperty("成交价")
    private BigDecimal price;

    @ApiModelProperty("成交数量")
    private BigDecimal qty;

    @ApiModelProperty("成交金额")
    private BigDecimal amt;

    @ApiModelProperty("手续费")
    private BigDecimal fee;

    private Integer closeFlag;
    private Integer longFlag;
    public PcOrderTradeDetailVo() {
    }

    public Integer getCloseFlag() {
        return closeFlag;
    }

    public void setCloseFlag(Integer closeFlag) {
        this.closeFlag = closeFlag;
    }

    public Integer getLongFlag() {
        return longFlag;
    }

    public void setLongFlag(Integer longFlag) {
        this.longFlag = longFlag;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeTime = tradeTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "PcOrderTradeDetailVo{" +
                "id=" + id +
                ", asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", tradeTime=" + tradeTime +
                ", price=" + price +
                ", qty=" + qty +
                ", amt=" + amt +
                ", fee=" + fee +
                '}';
    }
}
