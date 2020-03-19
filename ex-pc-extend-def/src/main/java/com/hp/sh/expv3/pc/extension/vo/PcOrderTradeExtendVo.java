package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/19
 */
public class PcOrderTradeExtendVo implements Serializable {

    @ApiModelProperty("自增主键")
    private Long id;
    @ApiModelProperty("资产")
    private String asset;
    @ApiModelProperty("交易对")
    private String symbol;
    @ApiModelProperty("成交价格")
    private BigDecimal price;
    @ApiModelProperty("数量")
    private BigDecimal number;
    @ApiModelProperty("成交时间")
    private Long tradeTime;
    @ApiModelProperty("1-marker， 0-taker")
    private Integer makerFlag;


    public PcOrderTradeExtendVo() {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public Long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Integer getMakerFlag() {
        return makerFlag;
    }

    public void setMakerFlag(Integer makerFlag) {
        this.makerFlag = makerFlag;
    }
}
