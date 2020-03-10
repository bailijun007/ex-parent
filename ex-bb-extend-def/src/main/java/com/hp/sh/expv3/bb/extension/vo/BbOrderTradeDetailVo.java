package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/9
 */
public class BbOrderTradeDetailVo implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    private Integer bidFlag;

    @ApiModelProperty("用户id")
    private Long userId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BbOrderTradeDetailVo() {
    }

    public Integer getBidFlag() {
        return bidFlag;
    }

    public void setBidFlag(Integer bidFlag) {
        this.bidFlag = bidFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
