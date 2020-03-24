package com.hp.sh.expv3.bb.kline.pojo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public class BbTradeVo implements Serializable {
    @ApiModelProperty("自增主键")
    private Long id;

    //资产
    @ApiModelProperty("资产")
    private String asset;

    //合约交易品种
    @ApiModelProperty("合约交易品种")
    private String symbol;


    @ApiModelProperty("事务Id")
    private Long matchTxId;

    @ApiModelProperty("taker账户ID")
    private Long tkAccountId;

    @ApiModelProperty("taker订单ID")
    private Long tkOrderId;



    @ApiModelProperty("maker账户Id")
    private Long mkAccountId;

    @ApiModelProperty("maker订单ID")
    private Long mkOrderId;


    @ApiModelProperty("成交价格")
    private BigDecimal price;

    @ApiModelProperty("数量")
    private BigDecimal number;

    @ApiModelProperty("成交时间")
    private Long tradeTime;





    @ApiModelProperty("taker是否买：1-是，0-否")
    private Long tkBidFlag;


    @ApiModelProperty("修改时间")
    private Long modified;

    @ApiModelProperty("创建时间")
    private Long created;

    public BbTradeVo() {
    }



    @Override
    public String toString() {
        return "BbTradeVo{" +
                "id=" + id +
                ", asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", matchTxId=" + matchTxId +
                ", tkAccountId=" + tkAccountId +
                ", tkOrderId=" + tkOrderId +
                ", mkAccountId=" + mkAccountId +
                ", mkOrderId=" + mkOrderId +
                ", price=" + price +
                ", number=" + number +
                ", tradeTime=" + tradeTime +
                ", tkBidFlag=" + tkBidFlag +
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

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public Long getTkAccountId() {
        return tkAccountId;
    }

    public void setTkAccountId(Long tkAccountId) {
        this.tkAccountId = tkAccountId;
    }

    public Long getTkOrderId() {
        return tkOrderId;
    }

    public void setTkOrderId(Long tkOrderId) {
        this.tkOrderId = tkOrderId;
    }


    public Long getMkAccountId() {
        return mkAccountId;
    }

    public void setMkAccountId(Long mkAccountId) {
        this.mkAccountId = mkAccountId;
    }

    public Long getMkOrderId() {
        return mkOrderId;
    }

    public void setMkOrderId(Long mkOrderId) {
        this.mkOrderId = mkOrderId;
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


    public Long getTkBidFlag() {
        return tkBidFlag;
    }

    public void setTkBidFlag(Long tkBidFlag) {
        this.tkBidFlag = tkBidFlag;
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
